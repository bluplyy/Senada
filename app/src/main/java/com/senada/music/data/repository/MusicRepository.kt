package com.senada.music.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.senada.music.data.model.Folder
import com.senada.music.data.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

interface MusicRepository {
    fun getSongsFlow(): Flow<List<Song>>
    fun getFoldersFlow(): Flow<List<Folder>>
    suspend fun loadSongs(): List<Song>
    suspend fun isSongAvailable(contentUri: Uri): Boolean
}

class MediaStoreMusicRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MusicRepository {

    override fun getSongsFlow(): Flow<List<Song>> = callbackFlow {
        // Initial scan
        trySend(querySongsInternal())

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                trySend(querySongsInternal())
            }
        }

        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }
        .debounce(400) // Debounce rapid media scanner updates
        .flowOn(ioDispatcher)

    override fun getFoldersFlow(): Flow<List<Folder>> {
        return getSongsFlow().map { songs ->
            groupSongsIntoFolders(songs)
        }
    }

    override suspend fun loadSongs(): List<Song> = withContext(ioDispatcher) {
        querySongsInternal()
    }

    override suspend fun isSongAvailable(contentUri: Uri): Boolean = withContext(ioDispatcher) {
        try {
            context.contentResolver.openFileDescriptor(contentUri, "r")?.use {
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun querySongsInternal(): List<Song> {
        val songsList = mutableListOf<Song>()
        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection.add(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
            projection.add(MediaStore.Audio.Media.BUCKET_ID)
            projection.add(MediaStore.Audio.Media.RELATIVE_PATH)
        } else {
            @Suppress("DEPRECATION")
            projection.add(MediaStore.Audio.Media.DATA)
        }

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 3000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection.toTypedArray(),
                selection,
                null,
                sortOrder
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dateAddedCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val albumIdCol = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

                val bucketNameCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    it.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
                } else -1

                val bucketIdCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    it.getColumnIndex(MediaStore.Audio.Media.BUCKET_ID)
                } else -1

                val relativePathCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    it.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)
                } else -1

                val dataCol = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    @Suppress("DEPRECATION")
                    it.getColumnIndex(MediaStore.Audio.Media.DATA)
                } else -1

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val title = it.getString(titleCol) ?: "Tanpa judul"
                    val artist = it.getString(artistCol) ?: "Penyanyi tidak diketahui"
                    val duration = it.getLong(durationCol)
                    val dateAdded = it.getLong(dateAddedCol)
                    val size = it.getLong(sizeCol)
                    val albumId = if (albumIdCol >= 0) it.getLong(albumIdCol) else 0L

                    val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    var folderName = "Music"
                    var bucketId = "default"
                    var volumePath = "Penyimpanan internal"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val bName = if (bucketNameCol >= 0) it.getString(bucketNameCol) else null
                        val bId = if (bucketIdCol >= 0) it.getString(bucketIdCol) else null
                        val relPath = if (relativePathCol >= 0) it.getString(relativePathCol) else null

                        folderName = bName ?: relPath?.trim('/')?.split('/')?.lastOrNull() ?: "Music"
                        bucketId = bId ?: folderName
                        volumePath = if (!relPath.isNullOrBlank()) "Penyimpanan internal / ${relPath.trim('/')}" else "Penyimpanan internal / $folderName"
                    } else if (dataCol >= 0) {
                        val path = it.getString(dataCol)
                        if (!path.isNullOrBlank()) {
                            val file = File(path)
                            val parent = file.parentFile
                            if (parent != null) {
                                folderName = parent.name
                                bucketId = parent.absolutePath
                                volumePath = "Penyimpanan internal / $folderName"
                            }
                        }
                    }

                    songsList.add(
                        Song(
                            id = id,
                            title = title,
                            artist = artist,
                            durationMs = duration,
                            dateAdded = dateAdded,
                            contentUri = uri,
                            folderName = folderName,
                            bucketId = bucketId,
                            volumePath = volumePath,
                            sizeBytes = size,
                            albumId = albumId
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Safe logging / graceful return on security exception
        }

        return songsList
    }

    private fun groupSongsIntoFolders(songs: List<Song>): List<Folder> {
        return songs.groupBy { it.bucketId }
            .map { (_, folderSongs) ->
                val first = folderSongs.first()
                Folder(
                    bucketId = first.bucketId,
                    name = first.folderName,
                    volumePath = first.volumePath,
                    songs = folderSongs.sortedBy { it.title.lowercase() }
                )
            }
            .sortedBy { it.name.lowercase() }
    }
}

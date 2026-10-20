package com.senada.music.data.model

import android.net.Uri
import java.util.Locale

/**
 * Representasi lagu lokal perangkat yang bersumber dari MediaStore.
 */
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val dateAdded: Long,
    val contentUriString: String = "",
    val folderName: String,
    val bucketId: String,
    val volumePath: String = "Penyimpanan internal",
    val sizeBytes: Long = 0L
) {
    constructor(
        id: Long,
        title: String,
        artist: String,
        durationMs: Long,
        dateAdded: Long,
        contentUri: Uri,
        folderName: String,
        bucketId: String,
        volumePath: String = "Penyimpanan internal",
        sizeBytes: Long = 0L
    ) : this(
        id = id,
        title = title,
        artist = artist,
        durationMs = durationMs,
        dateAdded = dateAdded,
        contentUriString = contentUri.toString(),
        folderName = folderName,
        bucketId = bucketId,
        volumePath = volumePath,
        sizeBytes = sizeBytes
    )

    val contentUri: Uri
        get() = try {
            Uri.parse(contentUriString)
        } catch (e: Throwable) {
            Uri.EMPTY
        }

    val displayArtist: String
        get() = if (artist.isBlank() || artist == "<unknown>") "Penyanyi tidak diketahui" else artist

    fun formattedDuration(): String {
        if (durationMs <= 0) return "0:00"
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}

/**
 * Mode perulangan (repeat) playback:
 * - OFF: tidak mengulang
 * - ALL: mengulang seluruh queue
 * - ONE: mengulang satu lagu aktif
 */
enum class RepeatMode {
    OFF,
    ALL,
    ONE;

    fun next(): RepeatMode = when (this) {
        OFF -> ALL
        ALL -> ONE
        ONE -> OFF
    }
}

/**
 * Folder asli perangkat yang berisi musik.
 */
data class Folder(
    val bucketId: String,
    val name: String,
    val volumePath: String,
    val songs: List<Song>
) {
    val songCount: Int get() = songs.size
}

/**
 * State pemutaran terpadu (single source of truth).
 */
data class PlaybackState(
    val activeSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleMode: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Song> = emptyList(),
    val isBuffering: Boolean = false,
    val errorMessage: String? = null
) {
    val formattedPosition: String
        get() = formatTime(currentPositionMs)

    val formattedDuration: String
        get() = formatTime(if (durationMs > 0) durationMs else (activeSong?.durationMs ?: 0L))

    private fun formatTime(ms: Long): String {
        if (ms <= 0) return "0:00"
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    val statusDescription: String
        get() {
            val shuffleText = if (shuffleMode) "Acak aktif" else "Acak nonaktif"
            val repeatText = when (repeatMode) {
                RepeatMode.OFF -> "Ulang nonaktif"
                RepeatMode.ALL -> "Ulang semua"
                RepeatMode.ONE -> "Ulang satu"
            }
            return "$shuffleText · $repeatText"
        }
}

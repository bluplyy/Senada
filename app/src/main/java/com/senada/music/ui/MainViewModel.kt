package com.senada.music.ui

import android.app.Application
import android.graphics.Bitmap
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.senada.music.data.artwork.ArtworkColors
import com.senada.music.data.artwork.ArtworkLoader
import com.senada.music.data.fixtures.MockMusicData
import com.senada.music.data.model.Folder
import com.senada.music.data.model.PlaybackState
import com.senada.music.data.model.Song
import com.senada.music.data.repository.MediaStoreMusicRepository
import com.senada.music.data.repository.MusicRepository
import com.senada.music.playback.MusicPlayerController
import com.senada.music.ui.components.NavTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PermissionStatus {
    INITIAL,        // 09 · Belum diminta
    GRANTED,        // Izin diberikan
    DENIED,         // 10 · Izin ditolak
    NEED_SETTINGS   // Ditolak permanen, perlu ke Pengaturan
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MusicRepository = MediaStoreMusicRepository(application)
    val artworkLoader = ArtworkLoader(application)
    val playerController = MusicPlayerController(application, viewModelScope)

    private val _permissionStatus = MutableStateFlow(PermissionStatus.INITIAL)
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState

    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _selectedFolder = MutableStateFlow<Folder?>(null)
    val selectedFolder: StateFlow<Folder?> = _selectedFolder.asStateFlow()

    private val _isFullPlayerVisible = MutableStateFlow(false)
    val isFullPlayerVisible: StateFlow<Boolean> = _isFullPlayerVisible.asStateFlow()

    private val _activeArtworkBitmap = MutableStateFlow<Bitmap?>(null)
    val activeArtworkBitmap: StateFlow<Bitmap?> = _activeArtworkBitmap.asStateFlow()

    private val _activeArtworkColors = MutableStateFlow(ArtworkColors.Default)
    val activeArtworkColors: StateFlow<ArtworkColors> = _activeArtworkColors.asStateFlow()

    // Cache bitmap untuk thumbnail di memori
    private val artworkCache = mutableMapOf<Long, Bitmap?>()

    init {
        // Pantau lagu aktif untuk memuat artwork dan warna gradasi
        viewModelScope.launch {
            playbackState.collectLatest { state ->
                val activeSong = state.activeSong
                if (activeSong != null) {
                    val bitmap = artworkLoader.loadArtwork(activeSong.id, activeSong.contentUri, 512)
                    _activeArtworkBitmap.value = bitmap
                    val colors = artworkLoader.extractColors(activeSong.id, activeSong.contentUri)
                    _activeArtworkColors.value = colors
                } else {
                    _activeArtworkBitmap.value = null
                    _activeArtworkColors.value = ArtworkColors.Default
                }
            }
        }
    }

    fun onPermissionGranted() {
        _permissionStatus.value = PermissionStatus.GRANTED
        startMediaScan()
    }

    fun onPermissionDenied(shouldShowRationale: Boolean) {
        _permissionStatus.value = if (shouldShowRationale) {
            PermissionStatus.DENIED
        } else {
            PermissionStatus.NEED_SETTINGS
        }
    }

    private fun startMediaScan() {
        viewModelScope.launch {
            _isScanning.value = true
            // Observasi flow koleksi dari MediaStore
            repository.getSongsFlow().collectLatest { songList ->
                _isScanning.value = false
                _songs.value = songList
                groupFolders(songList)
            }
        }
    }

    private fun groupFolders(songList: List<Song>) {
        val grouped = songList.groupBy { it.bucketId }
            .map { (_, fSongs) ->
                val first = fSongs.first()
                Folder(
                    bucketId = first.bucketId,
                    name = first.folderName,
                    volumePath = first.volumePath,
                    songs = fSongs.sortedBy { it.title.lowercase() }
                )
            }
            .sortedBy { it.name.lowercase() }
        _folders.value = grouped
    }

    fun loadMockDataForTesting() {
        _permissionStatus.value = PermissionStatus.GRANTED
        _songs.value = MockMusicData.sampleSongs
        _folders.value = MockMusicData.sampleFolders
    }

    fun getArtworkForSong(songId: Long): Bitmap? {
        return artworkCache[songId]
    }

    fun prefetchArtwork(song: Song) {
        if (!artworkCache.containsKey(song.id)) {
            viewModelScope.launch {
                val bmp = artworkLoader.loadArtwork(song.id, song.contentUri, 128)
                artworkCache[song.id] = bmp
            }
        }
    }

    fun playSong(song: Song, queue: List<Song>? = null) {
        val actualQueue = queue ?: _songs.value
        playerController.playSong(song, actualQueue)
    }

    fun togglePlayPause() {
        playerController.togglePlayPause()
    }

    fun skipToNext() {
        playerController.skipToNext()
    }

    fun skipToPrevious() {
        playerController.skipToPrevious()
    }

    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
    }

    fun toggleShuffle() {
        playerController.toggleShuffle()
    }

    fun cycleRepeatMode() {
        playerController.cycleRepeatMode()
    }

    fun openFullPlayer() {
        _isFullPlayerVisible.value = true
    }

    fun closeFullPlayer() {
        _isFullPlayerVisible.value = false
    }

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
        if (tab != NavTab.FOLDER) {
            _selectedFolder.value = null
        }
    }

    fun selectFolder(folder: Folder?) {
        _selectedFolder.value = folder
    }

    fun clearSnackbar() {
        playerController.clearErrorMessage()
    }

    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }
}

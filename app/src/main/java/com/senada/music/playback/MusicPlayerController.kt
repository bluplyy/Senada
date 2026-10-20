package com.senada.music.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.senada.music.data.model.PlaybackState
import com.senada.music.data.model.RepeatMode
import com.senada.music.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Controller penghubung antara UI dan MediaSessionService.
 * Menjamin hanya ada satu sumber kebenaran (single source of truth) untuk state pemutaran.
 */
class MusicPlayerController(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var positionUpdateJob: Job? = null
    private var currentSongsQueue: List<Song> = emptyList()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                controller = controllerFuture?.get()
                setupPlayerListener()
            } catch (ignored: Exception) {}
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        controller?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startPositionUpdates()
                } else {
                    stopPositionUpdates()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val mediaId = mediaItem?.mediaId?.toLongOrNull()
                val currentSong = currentSongsQueue.find { it.id == mediaId }
                _playbackState.update {
                    it.copy(
                        activeSong = currentSong,
                        durationMs = currentSong?.durationMs ?: 0L,
                        currentPositionMs = 0L
                    )
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                val isBuffering = state == Player.STATE_BUFFERING
                val isEnded = state == Player.STATE_ENDED
                _playbackState.update { it.copy(isBuffering = isBuffering) }
                if (isEnded) {
                    stopPositionUpdates()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                // Tampilkan snackbar peringatan file tidak tersedia
                _playbackState.update {
                    it.copy(
                        errorMessage = "File lagu tidak tersedia.\nMemutar lagu berikutnya."
                    )
                }
            }
        })
    }

    fun playSong(song: Song, queue: List<Song>) {
        currentSongsQueue = queue
        val currentCtrl = controller ?: return

        currentCtrl.clearMediaItems()
        val mediaItems = queue.map { qSong ->
            MediaItem.Builder()
                .setMediaId(qSong.id.toString())
                .setUri(qSong.contentUri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(qSong.title)
                        .setArtist(qSong.displayArtist)
                        .build()
                )
                .build()
        }

        currentCtrl.setMediaItems(mediaItems)
        val targetIndex = queue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        currentCtrl.seekTo(targetIndex, 0L)
        currentCtrl.prepare()
        currentCtrl.play()

        _playbackState.update {
            it.copy(
                activeSong = song,
                isPlaying = true,
                queue = queue,
                currentPositionMs = 0L,
                durationMs = song.durationMs,
                errorMessage = null
            )
        }
        startPositionUpdates()
    }

    fun togglePlayPause() {
        val currentCtrl = controller ?: return
        if (currentCtrl.isPlaying) {
            currentCtrl.pause()
        } else {
            currentCtrl.play()
        }
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun skipToNext() {
        val currentCtrl = controller ?: return
        if (currentCtrl.hasNextMediaItem()) {
            currentCtrl.seekToNextMediaItem()
        } else {
            // Jika berada di akhir queue dan bukan repeat one, jika repeat all putar dari awal
            if (_playbackState.value.repeatMode == RepeatMode.ALL && currentSongsQueue.isNotEmpty()) {
                currentCtrl.seekTo(0, 0L)
            }
        }
    }

    fun skipToPrevious() {
        val currentCtrl = controller ?: return
        if (currentCtrl.currentPosition > 3000) {
            currentCtrl.seekTo(0L)
        } else if (currentCtrl.hasPreviousMediaItem()) {
            currentCtrl.seekToPreviousMediaItem()
        } else {
            currentCtrl.seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _playbackState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun toggleShuffle() {
        val newShuffle = !_playbackState.value.shuffleMode
        controller?.shuffleModeEnabled = newShuffle
        _playbackState.update { it.copy(shuffleMode = newShuffle) }
    }

    fun cycleRepeatMode() {
        val nextMode = _playbackState.value.repeatMode.next()
        val exoRepeatMode = when (nextMode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
        controller?.repeatMode = exoRepeatMode
        _playbackState.update { it.copy(repeatMode = nextMode) }
    }

    fun clearErrorMessage() {
        _playbackState.update { it.copy(errorMessage = null) }
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                controller?.let { ctrl ->
                    if (ctrl.isPlaying) {
                        val pos = ctrl.currentPosition.coerceAtLeast(0L)
                        val dur = if (ctrl.duration > 0) ctrl.duration else (_playbackState.value.activeSong?.durationMs ?: 0L)
                        _playbackState.update {
                            it.copy(
                                currentPositionMs = pos,
                                durationMs = dur
                            )
                        }
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun release() {
        stopPositionUpdates()
        controller?.release()
        controller = null
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}

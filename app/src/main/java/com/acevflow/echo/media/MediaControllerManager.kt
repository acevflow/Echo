package com.acevflow.echo.media

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: UserPreferencesRepository
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val _controller = MutableStateFlow<MediaController?>(null)
    val controller = _controller.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    val currentMediaItem = _currentMediaItem.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition = _playbackPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _shuffleModeEnabled = MutableStateFlow(false)
    val shuffleModeEnabled = _shuffleModeEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode = _repeatMode.asStateFlow()

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                startProgressUpdate()
            } else {
                stopProgressUpdate()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentMediaItem.value = mediaItem
            _duration.value = _controller.value?.duration?.coerceAtLeast(0L) ?: 0L
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _duration.value = _controller.value?.duration?.coerceAtLeast(0L) ?: 0L
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _shuffleModeEnabled.value = shuffleModeEnabled
            scope.launch {
                preferencesRepository.setShuffleModeEnabled(shuffleModeEnabled)
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _repeatMode.value = repeatMode
            scope.launch {
                preferencesRepository.setRepeatMode(repeatMode)
            }
        }
    }

    fun initialize() {
        if (controllerFuture != null) return

        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            val controller = controllerFuture?.get()
            _controller.value = controller
            controller?.addListener(playerListener)
            
            // Restore states from preferences
            scope.launch {
                preferencesRepository.shuffleModeEnabled.collect { enabled ->
                    controller?.shuffleModeEnabled = enabled
                    _shuffleModeEnabled.value = enabled
                }
            }
            scope.launch {
                preferencesRepository.repeatMode.collect { mode ->
                    controller?.repeatMode = mode
                    _repeatMode.value = mode
                }
            }

            _isPlaying.value = controller?.isPlaying ?: false
            _currentMediaItem.value = controller?.currentMediaItem
            _duration.value = controller?.duration?.coerceAtLeast(0L) ?: 0L
            
            if (controller?.isPlaying == true) {
                startProgressUpdate()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _controller.value?.let {
                    _playbackPosition.value = it.currentPosition
                }
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playSong(mediaItem: MediaItem) {
        _controller.value?.run {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun setQueue(mediaItems: List<MediaItem>, startIndex: Int) {
        _controller.value?.run {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun pause() {
        _controller.value?.pause()
    }

    fun resume() {
        _controller.value?.play()
    }

    fun seekTo(position: Long) {
        _controller.value?.seekTo(position)
    }

    fun skipToNext() {
        _controller.value?.seekToNext()
    }

    fun skipToPrevious() {
        _controller.value?.seekToPrevious()
    }

    fun toggleShuffle() {
        _controller.value?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
        }
    }

    fun toggleRepeatMode() {
        _controller.value?.let {
            val nextMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                else -> Player.REPEAT_MODE_OFF
            }
            it.repeatMode = nextMode
        }
    }

    fun release() {
        stopProgressUpdate()
        _controller.value?.removeListener(playerListener)
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null
        _controller.value = null
    }
}

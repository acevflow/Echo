package com.acevflow.echo.media

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import com.google.common.util.concurrent.ListenableFuture
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

/**
 * A centralized manager that coordinates interaction between the UI and the [PlaybackService].
 * It maintains the [MediaController] connection and exposes the current playback state as [kotlinx.coroutines.flow.StateFlow].
 */
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

    private val _playlist = MutableStateFlow<List<MediaItem>>(emptyList())
    val playlist = _playlist.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition = _playbackPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _shuffleModeEnabled = MutableStateFlow(false)
    val shuffleModeEnabled = _shuffleModeEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode = _repeatMode.asStateFlow()

    private val _equalizerEnabled = MutableStateFlow(false)
    val equalizerEnabled = _equalizerEnabled.asStateFlow()

    private val _equalizerBands = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val equalizerBands = _equalizerBands.asStateFlow()

    private val _sleepTimerMillisLeft = MutableStateFlow<Long?>(null)
    val sleepTimerMillisLeft = _sleepTimerMillisLeft.asStateFlow()

    private var collectorsJob: Job? = null
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null
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

        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
            updatePlaylist()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _duration.value = _controller.value?.duration?.coerceAtLeast(0L) ?: 0L
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            if (_shuffleModeEnabled.value == shuffleModeEnabled) return
            _shuffleModeEnabled.value = shuffleModeEnabled
            scope.launch {
                preferencesRepository.setShuffleModeEnabled(shuffleModeEnabled)
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            if (_repeatMode.value == repeatMode) return
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
            collectorsJob?.cancel()
            collectorsJob = scope.launch {
                launch {
                    preferencesRepository.shuffleModeEnabled.collect { enabled ->
                        if (controller?.shuffleModeEnabled != enabled) {
                            controller?.shuffleModeEnabled = enabled
                        }
                        _shuffleModeEnabled.value = enabled
                    }
                }
                launch {
                    preferencesRepository.repeatMode.collect { mode ->
                        if (controller?.repeatMode != mode) {
                            controller?.repeatMode = mode
                        }
                        _repeatMode.value = mode
                    }
                }

                launch {
                    preferencesRepository.equalizerEnabled.collect { enabled ->
                        _equalizerEnabled.value = enabled
                    }
                }

                launch {
                    preferencesRepository.equalizerBands.collect { bands ->
                        _equalizerBands.value = bands
                    }
                }
            }

            _isPlaying.value = controller?.isPlaying ?: false
            _currentMediaItem.value = controller?.currentMediaItem
            _duration.value = controller?.duration?.coerceAtLeast(0L) ?: 0L
            updatePlaylist()
            
            if (controller?.isPlaying == true) {
                startProgressUpdate()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _controller.value?.let {
                    _playbackPosition.value = it.currentPosition
                }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updatePlaylist() {
        _controller.value?.let { controller ->
            val items = mutableListOf<MediaItem>()
            for (i in 0 until controller.mediaItemCount) {
                items.add(controller.getMediaItemAt(i))
            }
            _playlist.value = items
        }
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

    fun shuffleAll(mediaItems: List<MediaItem>) {
        _controller.value?.run {
            setMediaItems(mediaItems)
            shuffleModeEnabled = true
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

    fun playNext(mediaItem: MediaItem) {
        _controller.value?.run {
            val nextIndex = if (mediaItemCount > 0) currentMediaItemIndex + 1 else 0
            addMediaItem(nextIndex, mediaItem)
        }
    }

    fun addToQueue(mediaItem: MediaItem) {
        _controller.value?.addMediaItem(mediaItem)
    }

    fun removeFromQueue(index: Int) {
        _controller.value?.removeMediaItem(index)
    }

    fun moveMediaItem(fromIndex: Int, toIndex: Int) {
        _controller.value?.moveMediaItem(fromIndex, toIndex)
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

    fun toggleEqualizer() {
        scope.launch {
            preferencesRepository.setEqualizerEnabled(!_equalizerEnabled.value)
        }
    }

    fun setEqualizerBand(band: Int, gain: Int) {
        scope.launch {
            preferencesRepository.setEqualizerBand(band, gain)
        }
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        val totalMillis = minutes * 60 * 1000L
        val endTime = android.os.SystemClock.elapsedRealtime() + totalMillis
        _sleepTimerMillisLeft.value = totalMillis

        sleepTimerJob = scope.launch {
            while (isActive) {
                val remaining = endTime - android.os.SystemClock.elapsedRealtime()
                if (remaining <= 0) break
                _sleepTimerMillisLeft.value = remaining
                delay(1000)
            }
            pause()
            _sleepTimerMillisLeft.value = null
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerMillisLeft.value = null
    }

    fun release() {
        stopProgressUpdate()
        collectorsJob?.cancel()
        collectorsJob = null
        _controller.value?.removeListener(playerListener)
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null
        _controller.value = null
    }
}

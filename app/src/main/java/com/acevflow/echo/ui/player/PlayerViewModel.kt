package com.acevflow.echo.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the full-screen player.
 * Manages detailed playback state, seeking, and favorite status for the current track.
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    val isPlaying = mediaControllerManager.isPlaying
    val currentMediaItem = mediaControllerManager.currentMediaItem
    val playbackPosition = mediaControllerManager.playbackPosition
    val duration = mediaControllerManager.duration
    val shuffleModeEnabled = mediaControllerManager.shuffleModeEnabled
    val repeatMode = mediaControllerManager.repeatMode

    @OptIn(ExperimentalCoroutinesApi::class)
    val lyrics: StateFlow<String?> = currentMediaItem.flatMapLatest { mediaItem ->
        val songId = mediaItem?.mediaId?.toLongOrNull()
        if (songId != null) {
            musicRepository.getLyrics(songId)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: StateFlow<Boolean> = currentMediaItem.flatMapLatest { mediaItem ->
        val songId = mediaItem?.mediaId?.toLongOrNull()
        if (songId != null) {
            musicRepository.isFavorite(songId)
        } else {
            flowOf(false)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun play() = mediaControllerManager.resume()
    fun pause() = mediaControllerManager.pause()
    fun seekTo(position: Long) = mediaControllerManager.seekTo(position)
    fun skipToNext() = mediaControllerManager.skipToNext()
    fun skipToPrevious() = mediaControllerManager.skipToPrevious()
    fun toggleShuffle() = mediaControllerManager.toggleShuffle()
    fun toggleRepeatMode() = mediaControllerManager.toggleRepeatMode()

    fun toggleFavorite() {
        val songId = currentMediaItem.value?.mediaId?.toLongOrNull()
        if (songId != null) {
            viewModelScope.launch {
                musicRepository.toggleFavorite(songId)
            }
        }
    }
}

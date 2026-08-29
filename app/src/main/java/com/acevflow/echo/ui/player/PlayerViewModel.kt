package com.acevflow.echo.ui.player

import androidx.lifecycle.ViewModel
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    val isPlaying = mediaControllerManager.isPlaying
    val currentMediaItem = mediaControllerManager.currentMediaItem
    val playbackPosition = mediaControllerManager.playbackPosition
    val duration = mediaControllerManager.duration

    fun play() = mediaControllerManager.resume()
    fun pause() = mediaControllerManager.pause()
    fun seekTo(position: Long) = mediaControllerManager.seekTo(position)
}

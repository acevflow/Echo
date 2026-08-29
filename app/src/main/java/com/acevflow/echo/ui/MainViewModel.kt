package com.acevflow.echo.ui

import androidx.lifecycle.ViewModel
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    val isPlaying = mediaControllerManager.isPlaying
    val currentMediaItem = mediaControllerManager.currentMediaItem

    fun resume() {
        mediaControllerManager.resume()
    }

    fun pause() {
        mediaControllerManager.pause()
    }
}

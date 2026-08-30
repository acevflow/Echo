package com.acevflow.echo.ui.queue

import androidx.lifecycle.ViewModel
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    val playlist = mediaControllerManager.playlist
    val currentMediaItem = mediaControllerManager.currentMediaItem

    fun removeFromQueue(index: Int) {
        mediaControllerManager.removeFromQueue(index)
    }
}

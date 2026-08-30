package com.acevflow.echo.ui.settings

import androidx.lifecycle.ViewModel
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    val equalizerEnabled = mediaControllerManager.equalizerEnabled
    val equalizerBands = mediaControllerManager.equalizerBands

    fun toggleEqualizer() {
        mediaControllerManager.toggleEqualizer()
    }

    fun setBandLevel(band: Int, gain: Int) {
        mediaControllerManager.setEqualizerBand(band, gain)
    }
}

package com.acevflow.echo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import com.acevflow.echo.domain.model.EqPreset
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val equalizerEnabled = mediaControllerManager.equalizerEnabled
    val equalizerBands = mediaControllerManager.equalizerBands
    val selectedPreset = preferencesRepository.selectedEqPreset

    fun toggleEqualizer() {
        mediaControllerManager.toggleEqualizer()
    }

    fun setBandLevel(band: Int, gain: Int) {
        mediaControllerManager.setEqualizerBand(band, gain)
    }

    fun applyPreset(preset: EqPreset) {
        viewModelScope.launch {
            preferencesRepository.setEqualizerPreset(preset.name, preset.gains)
        }
    }
}

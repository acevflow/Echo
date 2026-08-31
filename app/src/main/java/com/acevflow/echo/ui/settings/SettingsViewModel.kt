package com.acevflow.echo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import com.acevflow.echo.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val themeMode = preferencesRepository.themeMode
    val dynamicColorEnabled = preferencesRepository.dynamicColorEnabled
    val crossfadeDuration = preferencesRepository.crossfadeDuration
    val excludedFolders = preferencesRepository.excludedFolders

    val allFolders = musicRepository.getFolders()

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDynamicColorEnabled(enabled)
        }
    }

    fun setCrossfadeDuration(duration: Int) {
        viewModelScope.launch {
            preferencesRepository.setCrossfadeDuration(duration)
        }
    }

    fun toggleFolderExclusion(folderPath: String) {
        viewModelScope.launch {
            preferencesRepository.toggleFolderExclusion(folderPath)
        }
    }
}

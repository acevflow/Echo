package com.acevflow.echo.ui

import androidx.lifecycle.ViewModel
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val isPlaying = mediaControllerManager.isPlaying
    val currentMediaItem = mediaControllerManager.currentMediaItem
    
    val themeMode = preferencesRepository.themeMode
    val dynamicColorEnabled = preferencesRepository.dynamicColorEnabled
    
    val sleepTimerMillisLeft = mediaControllerManager.sleepTimerMillisLeft

    init {
        mediaControllerManager.initialize()
    }

    fun resume() {
        mediaControllerManager.resume()
    }

    fun pause() {
        mediaControllerManager.pause()
    }

    fun startSleepTimer(minutes: Int) {
        mediaControllerManager.startSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        mediaControllerManager.cancelSleepTimer()
    }
}

package com.acevflow.echo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.data.repository.ShortcutRepository
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Top-level ViewModel that provides global playback state and user preferences
 * to the entire application shell.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val preferencesRepository: UserPreferencesRepository,
    private val musicRepository: MusicRepository,
    private val shortcutRepository: ShortcutRepository
) : ViewModel() {

    val isPlaying = mediaControllerManager.isPlaying
    val currentMediaItem = mediaControllerManager.currentMediaItem
    
    val themeMode = preferencesRepository.themeMode
    val dynamicColorEnabled = preferencesRepository.dynamicColorEnabled
    
    val playbackPosition = mediaControllerManager.playbackPosition
    val duration = mediaControllerManager.duration
    val sleepTimerMillisLeft = mediaControllerManager.sleepTimerMillisLeft

    init {
        mediaControllerManager.initialize()
        
        musicRepository.getPlaylists()
            .onEach { shortcutRepository.updateDynamicShortcuts(it) }
            .launchIn(viewModelScope)
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

    fun shuffleAll() {
        viewModelScope.launch {
            val songs = musicRepository.getSongs().first()
            mediaControllerManager.shuffleAll(songs.map { toMediaItem(it) })
        }
    }

    fun playPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val songs = musicRepository.getSongsInPlaylist(playlistId).first()
            if (songs.isNotEmpty()) {
                mediaControllerManager.setQueue(songs.map { toMediaItem(it) }, 0)
            }
        }
    }

    private fun toMediaItem(song: Song): MediaItem {
        return MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.contentUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .setArtworkUri(song.artworkUri)
                    .build()
            )
            .build()
    }
}

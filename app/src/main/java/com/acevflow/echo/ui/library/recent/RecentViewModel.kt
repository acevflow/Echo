package com.acevflow.echo.ui.library.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecentUiState>(RecentUiState.Loading)
    val uiState: StateFlow<RecentUiState> = _uiState.asStateFlow()

    init {
        loadRecentSongs()
    }

    private fun loadRecentSongs() {
        viewModelScope.launch {
            musicRepository.getRecentHistory().collect { songs ->
                _uiState.value = if (songs.isEmpty()) {
                    RecentUiState.Empty
                } else {
                    RecentUiState.Success(songs)
                }
            }
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
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
        mediaControllerManager.setQueue(mediaItems, startIndex)
    }

    fun clearHistory() {
        viewModelScope.launch {
            musicRepository.clearHistory()
        }
    }
}

sealed interface RecentUiState {
    data object Loading : RecentUiState
    data object Empty : RecentUiState
    data class Success(val songs: List<Song>) : RecentUiState
}

package com.acevflow.echo.ui.library

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
class LibraryViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        mediaControllerManager.initialize()
    }

    fun loadSongs() {
        viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            musicRepository.getSongs().collect { songs ->
                _uiState.value = if (songs.isEmpty()) {
                    LibraryUiState.Empty
                } else {
                    LibraryUiState.Success(songs)
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

    override fun onCleared() {
        super.onCleared()
        mediaControllerManager.release()
    }
}

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Success(val songs: List<Song>) : LibraryUiState
}

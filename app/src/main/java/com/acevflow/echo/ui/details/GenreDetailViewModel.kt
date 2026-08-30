package com.acevflow.echo.ui.details

import androidx.lifecycle.SavedStateHandle
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
class GenreDetailViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val genreId: Long = checkNotNull(savedStateHandle["genreId"])

    private val _uiState = MutableStateFlow<GenreDetailUiState>(GenreDetailUiState.Loading)
    val uiState: StateFlow<GenreDetailUiState> = _uiState.asStateFlow()

    init {
        loadGenreSongs()
    }

    private fun loadGenreSongs() {
        viewModelScope.launch {
            musicRepository.getSongsByGenre(genreId).collect { songs ->
                _uiState.value = if (songs.isEmpty()) {
                    GenreDetailUiState.Empty
                } else {
                    GenreDetailUiState.Success(songs)
                }
            }
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int) {
        val mediaItems = songs.map { toMediaItem(it) }
        mediaControllerManager.setQueue(mediaItems, startIndex)
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

sealed interface GenreDetailUiState {
    data object Loading : GenreDetailUiState
    data object Empty : GenreDetailUiState
    data class Success(val songs: List<Song>) : GenreDetailUiState
}

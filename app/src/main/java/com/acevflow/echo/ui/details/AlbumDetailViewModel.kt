package com.acevflow.echo.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val albumId: Long = checkNotNull(savedStateHandle["albumId"])

    private val _uiState = MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    init {
        loadAlbumSongs()
    }

    private fun loadAlbumSongs() {
        viewModelScope.launch {
            musicRepository.getSongsByAlbum(albumId).collect { songs ->
                _uiState.value = if (songs.isEmpty()) {
                    AlbumDetailUiState.Empty
                } else {
                    AlbumDetailUiState.Success(songs)
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
}

sealed interface AlbumDetailUiState {
    data object Loading : AlbumDetailUiState
    data object Empty : AlbumDetailUiState
    data class Success(val songs: List<Song>) : AlbumDetailUiState
}

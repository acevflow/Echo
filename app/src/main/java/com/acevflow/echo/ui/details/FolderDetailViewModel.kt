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
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class FolderDetailViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val encodedPath: String = checkNotNull(savedStateHandle["folderPath"])
    private val folderPath = URLDecoder.decode(encodedPath, "UTF-8")

    private val _uiState = MutableStateFlow<FolderDetailUiState>(FolderDetailUiState.Loading)
    val uiState: StateFlow<FolderDetailUiState> = _uiState.asStateFlow()

    init {
        loadFolderSongs()
    }

    private fun loadFolderSongs() {
        viewModelScope.launch {
            musicRepository.getSongsByFolder(folderPath).collect { songs ->
                _uiState.value = if (songs.isEmpty()) {
                    FolderDetailUiState.Empty
                } else {
                    FolderDetailUiState.Success(songs)
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

sealed interface FolderDetailUiState {
    data object Loading : FolderDetailUiState
    data object Empty : FolderDetailUiState
    data class Success(val songs: List<Song>) : FolderDetailUiState
}

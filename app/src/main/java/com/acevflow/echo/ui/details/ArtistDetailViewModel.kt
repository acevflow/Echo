package com.acevflow.echo.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Album
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistName: String = checkNotNull(savedStateHandle["artistName"])

    private val _uiState = MutableStateFlow<ArtistDetailUiState>(ArtistDetailUiState.Loading)
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    init {
        loadArtistAlbums()
    }

    private fun loadArtistAlbums() {
        viewModelScope.launch {
            musicRepository.getAlbumsByArtist(artistName).collect { albums ->
                _uiState.value = if (albums.isEmpty()) {
                    ArtistDetailUiState.Empty
                } else {
                    ArtistDetailUiState.Success(albums)
                }
            }
        }
    }
}

sealed interface ArtistDetailUiState {
    data object Loading : ArtistDetailUiState
    data object Empty : ArtistDetailUiState
    data class Success(val albums: List<Album>) : ArtistDetailUiState
}

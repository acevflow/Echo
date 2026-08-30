package com.acevflow.echo.ui.library.albums

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
class AlbumsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumsUiState>(AlbumsUiState.Loading)
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            musicRepository.getAlbums().collect { albums ->
                _uiState.value = if (albums.isEmpty()) {
                    AlbumsUiState.Empty
                } else {
                    AlbumsUiState.Success(albums)
                }
            }
        }
    }
}

sealed interface AlbumsUiState {
    data object Loading : AlbumsUiState
    data object Empty : AlbumsUiState
    data class Success(val albums: List<Album>) : AlbumsUiState
}

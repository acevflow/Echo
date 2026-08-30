package com.acevflow.echo.ui.library.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Artist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArtistsUiState>(ArtistsUiState.Loading)
    val uiState: StateFlow<ArtistsUiState> = _uiState.asStateFlow()

    init {
        loadArtists()
    }

    private fun loadArtists() {
        viewModelScope.launch {
            musicRepository.getArtists().collect { artists ->
                _uiState.value = if (artists.isEmpty()) {
                    ArtistsUiState.Empty
                } else {
                    ArtistsUiState.Success(artists)
                }
            }
        }
    }
}

sealed interface ArtistsUiState {
    data object Loading : ArtistsUiState
    data object Empty : ArtistsUiState
    data class Success(val artists: List<Artist>) : ArtistsUiState
}

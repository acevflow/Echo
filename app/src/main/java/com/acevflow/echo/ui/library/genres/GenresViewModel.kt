package com.acevflow.echo.ui.library.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GenresUiState>(GenresUiState.Loading)
    val uiState: StateFlow<GenresUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            musicRepository.getGenres().collect { genres ->
                _uiState.value = if (genres.isEmpty()) {
                    GenresUiState.Empty
                } else {
                    GenresUiState.Success(genres)
                }
            }
        }
    }
}

sealed interface GenresUiState {
    data object Loading : GenresUiState
    data object Empty : GenresUiState
    data class Success(val genres: List<Genre>) : GenresUiState
}

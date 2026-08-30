package com.acevflow.echo.ui.library.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Folder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FoldersUiState>(FoldersUiState.Loading)
    val uiState: StateFlow<FoldersUiState> = _uiState.asStateFlow()

    init {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            musicRepository.getFolders().collect { folders ->
                _uiState.value = if (folders.isEmpty()) {
                    FoldersUiState.Empty
                } else {
                    FoldersUiState.Success(folders)
                }
            }
        }
    }
}

sealed interface FoldersUiState {
    data object Loading : FoldersUiState
    data object Empty : FoldersUiState
    data class Success(val folders: List<Folder>) : FoldersUiState
}

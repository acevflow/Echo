package com.acevflow.echo.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSongViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId: Long = checkNotNull(savedStateHandle["songId"])

    private val _uiState = MutableStateFlow<EditSongUiState>(EditSongUiState.Loading)
    val uiState: StateFlow<EditSongUiState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _artist = MutableStateFlow("")
    val artist = _artist.asStateFlow()

    private val _album = MutableStateFlow("")
    val album = _album.asStateFlow()

    private val _artworkUri = MutableStateFlow<android.net.Uri?>(null)
    val artworkUri = _artworkUri.asStateFlow()

    private val _pendingIntent = MutableStateFlow<android.app.PendingIntent?>(null)
    val pendingIntent = _pendingIntent.asStateFlow()

    init {
        loadSong()
    }

    private fun loadSong() {
        viewModelScope.launch {
            val song = musicRepository.getSongById(songId).first()
            if (song != null) {
                _title.value = song.title
                _artist.value = song.artist
                _album.value = song.album
                _artworkUri.value = song.artworkUri
                _uiState.value = EditSongUiState.Success
            } else {
                _uiState.value = EditSongUiState.Error("Song not found")
            }
        }
    }

    fun onTitleChange(value: String) { _title.value = value }
    fun onArtistChange(value: String) { _artist.value = value }
    fun onAlbumChange(value: String) { _album.value = value }
    fun onArtworkChange(uri: android.net.Uri?) { _artworkUri.value = uri }

    fun saveChanges() {
        viewModelScope.launch {
            val intent = musicRepository.createWriteRequest(songId)
            if (intent != null) {
                _pendingIntent.value = intent
            } else {
                performUpdate()
            }
        }
    }

    fun onPermissionGranted() {
        _pendingIntent.value = null
        performUpdate()
    }

    fun onPermissionDenied() {
        _pendingIntent.value = null
    }

    private fun performUpdate() {
        viewModelScope.launch {
            musicRepository.updateSongMetadata(songId, _title.value, _artist.value, _album.value)
            _artworkUri.value?.let { 
                musicRepository.updateSongArtwork(songId, it)
            }
            _uiState.value = EditSongUiState.Saved
        }
    }
}

sealed interface EditSongUiState {
    data object Loading : EditSongUiState
    data object Success : EditSongUiState
    data class Error(val message: String) : EditSongUiState
    data object Saved : EditSongUiState
}

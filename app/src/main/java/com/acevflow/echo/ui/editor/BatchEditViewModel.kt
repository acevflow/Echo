package com.acevflow.echo.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatchEditViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BatchEditUiState>(BatchEditUiState.Idle)
    val uiState: StateFlow<BatchEditUiState> = _uiState.asStateFlow()

    private val _artist = MutableStateFlow("")
    val artist = _artist.asStateFlow()

    private val _album = MutableStateFlow("")
    val album = _album.asStateFlow()

    private val _pendingIntent = MutableStateFlow<android.app.PendingIntent?>(null)
    val pendingIntent = _pendingIntent.asStateFlow()

    private var selectedSongIds: List<Long> = emptyList()

    fun setup(songIds: List<Long>) {
        selectedSongIds = songIds
        viewModelScope.launch {
            val songs = mutableListOf<Song>()
            songIds.forEach { id ->
                musicRepository.getSongById(id).first()?.let { songs.add(it) }
            }
            
            if (songs.isNotEmpty()) {
                // Find common artist/album
                val firstArtist = songs.first().artist
                val allSameArtist = songs.all { it.artist == firstArtist }
                _artist.value = if (allSameArtist) firstArtist else ""

                val firstAlbum = songs.first().album
                val allSameAlbum = songs.all { it.album == firstAlbum }
                _album.value = if (allSameAlbum) firstAlbum else ""
            }
        }
    }

    fun onArtistChange(value: String) { _artist.value = value }
    fun onAlbumChange(value: String) { _album.value = value }

    fun saveChanges() {
        viewModelScope.launch {
            val intent = musicRepository.createBatchWriteRequest(selectedSongIds)
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
            musicRepository.updateBatchMetadata(
                selectedSongIds,
                _artist.value.takeIf { it.isNotBlank() },
                _album.value.takeIf { it.isNotBlank() }
            )
            _uiState.value = BatchEditUiState.Saved
        }
    }
}

sealed interface BatchEditUiState {
    data object Idle : BatchEditUiState
    data object Saved : BatchEditUiState
}

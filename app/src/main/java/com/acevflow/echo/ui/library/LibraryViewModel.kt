package com.acevflow.echo.ui.library

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    private val _selectedSongIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedSongIds: StateFlow<Set<Long>> = _selectedSongIds.asStateFlow()

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val playlists = musicRepository.getPlaylists()
    val recentlyAdded = musicRepository.getRecentlyAdded()
    val mostPlayed = musicRepository.getMostPlayed()

    init {
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            musicRepository.getSongs().collect { songs ->
                _uiState.value = if (songs.isEmpty()) {
                    LibraryUiState.Empty
                } else {
                    LibraryUiState.Success(songs)
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

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            musicRepository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun playNext(song: Song) {
        val mediaItem = toMediaItem(song)
        mediaControllerManager.playNext(mediaItem)
    }

    fun addToQueue(song: Song) {
        val mediaItem = toMediaItem(song)
        mediaControllerManager.addToQueue(mediaItem)
    }

    fun toggleSelection(songId: Long) {
        val current = _selectedSongIds.value
        _selectedSongIds.value = if (current.contains(songId)) {
            current - songId
        } else {
            current + songId
        }
    }

    fun clearSelection() {
        _selectedSongIds.value = emptySet()
    }

    fun addSelectedToQueue(allSongs: List<Song>) {
        val selected = allSongs.filter { _selectedSongIds.value.contains(it.id) }
        viewModelScope.launch {
            selected.forEach { song ->
                mediaControllerManager.addToQueue(toMediaItem(song))
            }
            clearSelection()
        }
    }

    fun addSelectedToPlaylist(playlistId: Long, allSongs: List<Song>) {
        val selectedIds = _selectedSongIds.value
        viewModelScope.launch {
            selectedIds.forEach { songId ->
                musicRepository.addSongToPlaylist(playlistId, songId)
            }
            clearSelection()
        }
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

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Success(val songs: List<Song>) : LibraryUiState
}

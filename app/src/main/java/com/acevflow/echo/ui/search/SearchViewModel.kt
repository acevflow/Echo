package com.acevflow.echo.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.media.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val searchHistory = musicRepository.getRecentSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val searchResults: StateFlow<SearchResults> = combine(
        _query.debounce(300),
        musicRepository.getSongs(),
        musicRepository.getAlbums(),
        musicRepository.getArtists()
    ) { query, songs, albums, artists ->
        if (query.isBlank()) {
            SearchResults.Empty
        } else {
            val filteredSongs = songs.filter { 
                it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
            }
            val filteredAlbums = albums.filter { 
                it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
            }
            val filteredArtists = artists.filter { 
                it.name.contains(query, ignoreCase = true)
            }
            
            if (filteredSongs.isEmpty() && filteredAlbums.isEmpty() && filteredArtists.isEmpty()) {
                SearchResults.NoResults
            } else {
                SearchResults.Success(filteredSongs, filteredAlbums, filteredArtists)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResults.Empty)

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun deleteHistoryItem(query: String) {
        viewModelScope.launch {
            musicRepository.deleteSearchQuery(query)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            musicRepository.clearSearchHistory()
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            musicRepository.addSearchQuery(_query.value)
        }
        val mediaItem = MediaItem.Builder()
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
        
        mediaControllerManager.playSong(mediaItem)
    }
}

sealed interface SearchResults {
    data object Empty : SearchResults
    data object NoResults : SearchResults
    data class Success(
        val songs: List<Song>,
        val albums: List<Album>,
        val artists: List<Artist>
    ) : SearchResults
}

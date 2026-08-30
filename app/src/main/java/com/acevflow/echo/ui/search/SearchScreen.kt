package com.acevflow.echo.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.ui.library.LibraryViewModel
import com.acevflow.echo.ui.components.EchoSongItem
import com.acevflow.echo.ui.components.EchoAlbumItem
import com.acevflow.echo.ui.components.EchoArtistItem
import com.acevflow.echo.ui.theme.Dims

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = query,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSearch = { },
                active = false,
                onActiveChange = { },
                placeholder = { Text("Search your library") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dims.ScreenPadding)
            ) { }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val results = searchResults) {
                    SearchResults.Empty -> {
                        if (searchHistory.isNotEmpty()) {
                            SearchHistoryContent(
                                history = searchHistory,
                                onHistoryClick = { viewModel.onQueryChange(it) },
                                onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                                onClearHistory = { viewModel.clearHistory() }
                            )
                        } else {
                            Text(
                                text = "Discover your music",
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    SearchResults.NoResults -> {
                        Text(
                            text = "No results for \"$query\"",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is SearchResults.Success -> {
                        SearchContent(
                            viewModel = hiltViewModel(),
                            songs = results.songs,
                            albums = results.albums,
                            artists = results.artists,
                            onSongClick = { viewModel.playSong(it) },
                            onAlbumClick = onAlbumClick,
                            onArtistClick = onArtistClick,
                            onEditInfo = onNavigateToEdit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHistoryContent(
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onDeleteHistoryItem: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dims.ScreenPadding),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            androidx.compose.material3.TextButton(onClick = onClearHistory) {
                Text("Clear All")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history, key = { it }) { query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryClick(query) }
                        .padding(horizontal = Dims.ScreenPadding, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = query,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = Dims.ElementPadding),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    androidx.compose.material3.IconButton(
                        onClick = { onDeleteHistoryItem(query) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchContent(
    viewModel: LibraryViewModel,
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onEditInfo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dims.MiniPlayerHeight + Dims.ScreenPadding)
    ) {
        if (songs.isNotEmpty()) {
            item {
                SectionHeader("Songs")
            }
            items(songs, key = { "song_${it.id}" }) { song ->
                EchoSongItem(
                    song = song,
                    onClick = { onSongClick(song) },
                    onPlayNext = { viewModel.playNext(song) },
                    onAddToQueue = { viewModel.addToQueue(song) },
                    onAddToPlaylist = { /* Could be implemented */ },
                    onEditInfo = { onEditInfo(song.id) }
                )
            }
        }

        if (albums.isNotEmpty()) {
            item {
                SectionHeader("Albums")
            }
            items(albums, key = { "album_${it.id}" }) { album ->
                Box(modifier = Modifier.padding(horizontal = Dims.ScreenPadding, vertical = Dims.SmallPadding)) {
                    EchoAlbumItem(
                        album = album,
                        modifier = Modifier.clickable { onAlbumClick(album) }
                    )
                }
            }
        }

        if (artists.isNotEmpty()) {
            item {
                SectionHeader("Artists")
            }
            items(artists, key = { "artist_${it.id}" }) { artist ->
                EchoArtistItem(
                    artist = artist,
                    modifier = Modifier.clickable { onArtistClick(artist) }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = Dims.ScreenPadding, vertical = Dims.ElementPadding)
    )
}

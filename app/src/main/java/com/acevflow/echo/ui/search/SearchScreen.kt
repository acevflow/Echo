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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.ui.library.SongItem
import com.acevflow.echo.ui.library.albums.AlbumItem
import com.acevflow.echo.ui.library.artists.ArtistItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = { viewModel.onQueryChange(it) },
            onSearch = { },
            active = false,
            onActiveChange = { },
            placeholder = { Text("Search your library") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) { }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val results = searchResults) {
                SearchResults.Empty -> {
                    Text(
                        text = "Search by song, album, or artist.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                SearchResults.NoResults -> {
                    Text(
                        text = "No results found for \"$query\"",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is SearchResults.Success -> {
                    SearchContent(
                        songs = results.songs,
                        albums = results.albums,
                        artists = results.artists,
                        onSongClick = { viewModel.playSong(it) },
                        onAlbumClick = onAlbumClick,
                        onArtistClick = onArtistClick
                    )
                }
            }
        }
    }
}

@Composable
fun SearchContent(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (songs.isNotEmpty()) {
            item {
                Text(
                    text = "Songs",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(songs, key = { "song_${it.id}" }) { song ->
                SongItem(
                    song = song,
                    onAddToPlaylist = { /* Implementation for search could be added later */ },
                    modifier = Modifier
                        .clickable { onSongClick(song) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        if (albums.isNotEmpty()) {
            item {
                Text(
                    text = "Albums",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(albums, key = { "album_${it.id}" }) { album ->
                AlbumItem(
                    album = album,
                    modifier = Modifier
                        .clickable { onAlbumClick(album) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        if (artists.isNotEmpty()) {
            item {
                Text(
                    text = "Artists",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(artists, key = { "artist_${it.id}" }) { artist ->
                ArtistItem(
                    artist = artist,
                    modifier = Modifier
                        .clickable { onArtistClick(artist) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

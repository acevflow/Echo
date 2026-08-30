package com.acevflow.echo.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.ui.library.AddToPlaylistDialog
import com.acevflow.echo.ui.library.SongList

@Composable
fun AlbumDetailScreen(
    viewModel: AlbumDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val playlists by viewModel.playlists.collectAsState(initial = emptyList())
    
    var songForPlaylist by remember { mutableStateOf<Song?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            AlbumDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            AlbumDetailUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No songs found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is AlbumDetailUiState.Success -> {
                SongList(
                    viewModel = hiltViewModel(),
                    songs = state.songs,
                    onSongClick = { index ->
                        viewModel.playSongs(state.songs, index)
                    },
                    onAddToPlaylist = { song ->
                        songForPlaylist = song
                    }
                )
            }
        }
    }

    songForPlaylist?.let { song ->
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { songForPlaylist = null },
            onPlaylistSelected = { playlist ->
                viewModel.addSongToPlaylist(playlist.id, song.id)
                songForPlaylist = null
            }
        )
    }
}

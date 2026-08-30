package com.acevflow.echo.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.acevflow.echo.ui.library.SongList

@Composable
fun PlaylistDetailScreen(
    viewModel: PlaylistDetailViewModel,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            PlaylistDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            PlaylistDetailUiState.Empty -> {
                Text(
                    text = "This playlist is empty.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is PlaylistDetailUiState.Success -> {
                SongList(
                    viewModel = hiltViewModel(),
                    songs = state.songs,
                    onSongClick = { index ->
                        viewModel.playSongs(state.songs, index)
                    },
                    onAddToPlaylist = { /* No-op for now, or could be "Remove" */ },
                    onEditInfo = { song ->
                        onNavigateToEdit(song.id)
                    }
                )
            }
        }
    }
}

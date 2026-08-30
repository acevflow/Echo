package com.acevflow.echo.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.acevflow.echo.ui.library.SongList

@Composable
fun FolderDetailScreen(
    viewModel: FolderDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            FolderDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            FolderDetailUiState.Empty -> {
                Text(
                    text = "No songs found in this folder.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is FolderDetailUiState.Success -> {
                SongList(
                    viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
                    songs = state.songs,
                    onSongClick = { index ->
                        viewModel.playSongs(state.songs, index)
                    },
                    onAddToPlaylist = { /* Could be implemented */ }
                )
            }
        }
    }
}

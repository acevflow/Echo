package com.acevflow.echo.ui.library.recent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.acevflow.echo.ui.library.SongList

@Composable
fun RecentScreen(
    viewModel: RecentViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (uiState is RecentUiState.Success) {
                FloatingActionButton(onClick = { viewModel.clearHistory() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear History")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                RecentUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                RecentUiState.Empty -> {
                    Text(
                        text = "Your listening history is empty.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is RecentUiState.Success -> {
                    SongList(
                        songs = state.songs,
                        onSongClick = { index ->
                            viewModel.playSongs(state.songs, index)
                        },
                        onAddToPlaylist = { /* Could be implemented later */ }
                    )
                }
            }
        }
    }
}

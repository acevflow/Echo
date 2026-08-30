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
import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.ui.library.albums.AlbumGrid

@Composable
fun ArtistDetailScreen(
    viewModel: ArtistDetailViewModel,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            ArtistDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            ArtistDetailUiState.Empty -> {
                Text(
                    text = "No albums found for this artist.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is ArtistDetailUiState.Success -> {
                AlbumGrid(
                    albums = state.albums,
                    onAlbumClick = onAlbumClick
                )
            }
        }
    }
}

package com.acevflow.echo.ui.library.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.ui.components.EchoAlbumItem
import com.acevflow.echo.ui.theme.Dims

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            AlbumsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            AlbumsUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No albums found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is AlbumsUiState.Success -> {
                AlbumGrid(
                    albums = state.albums,
                    onAlbumClick = onAlbumClick
                )
            }
        }
    }
}

@Composable
fun AlbumGrid(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dims.ScreenPadding,
            end = Dims.ScreenPadding,
            top = Dims.ScreenPadding,
            bottom = Dims.MiniPlayerHeight + Dims.ScreenPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(Dims.ElementPadding),
        verticalArrangement = Arrangement.spacedBy(Dims.ElementPadding)
    ) {
        items(albums, key = { it.id }) { album ->
            EchoAlbumItem(
                album = album,
                modifier = Modifier.clickable { onAlbumClick(album) }
            )
        }
    }
}

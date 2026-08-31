package com.acevflow.echo.ui.library.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.ui.components.EchoArtistItem
import com.acevflow.echo.ui.theme.Dims

@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel,
    onArtistClick: (Artist) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            ArtistsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            ArtistsUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No artists found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is ArtistsUiState.Success -> {
                ArtistList(
                    artists = state.artists,
                    onArtistClick = onArtistClick
                )
            }
        }
    }
}

@Composable
fun ArtistList(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = Dims.TinyPadding,
            bottom = Dims.MiniPlayerHeight + Dims.ScreenPadding
        )
    ) {
        itemsIndexed(artists, key = { _, artist -> artist.id }) { _, artist ->
            EchoArtistItem(
                artist = artist,
                modifier = Modifier.clickable { onArtistClick(artist) }
            )
        }
    }
}

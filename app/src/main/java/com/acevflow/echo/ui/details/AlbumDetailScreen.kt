package com.acevflow.echo.ui.details

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.ui.library.AddToPlaylistDialog
import com.acevflow.echo.ui.library.SongList
import com.acevflow.echo.ui.navigation.LocalNavAnimatedVisibilityScope
import com.acevflow.echo.ui.navigation.LocalSharedTransitionScope
import com.acevflow.echo.ui.theme.Dims

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumDetailScreen(
    viewModel: AlbumDetailViewModel,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val playlists by viewModel.playlists.collectAsState(initial = emptyList())
    
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
    
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
                Column(modifier = Modifier.fillMaxSize()) {
                    val firstSong = state.songs.firstOrNull()
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dims.ScreenPadding, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imageModifier = Modifier
                            .size(Dims.ArtworkMedium)
                            .clip(RoundedCornerShape(Dims.CardRadius))

                        Box {
                            AsyncImage(
                                model = firstSong?.artworkUri,
                                contentDescription = null,
                                modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null && firstSong != null) {
                                    with(sharedTransitionScope) {
                                        imageModifier.sharedBounds(
                                            rememberSharedContentState(key = "artwork_${firstSong.id}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                    }
                                } else imageModifier,
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(Dims.ElementPadding))

                        Column {
                            Text(
                                text = firstSong?.album ?: "Unknown Album",
                                style = MaterialTheme.typography.headlineMedium,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                text = firstSong?.artist ?: "Unknown Artist",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${state.songs.size} songs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }

                    SongList(
                        viewModel = hiltViewModel(),
                        songs = state.songs,
                        onSongClick = { index ->
                            viewModel.playSongs(state.songs, index)
                        },
                        onAddToPlaylist = { song ->
                            songForPlaylist = song
                        },
                        onEditInfo = { song ->
                            onNavigateToEdit(song.id)
                        }
                    )
                }
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

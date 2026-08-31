package com.acevflow.echo.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.acevflow.echo.ui.MainViewModel
import com.acevflow.echo.ui.navigation.LocalSharedTransitionScope
import com.acevflow.echo.ui.theme.Dims

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MiniPlayer(
    viewModel: MainViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.playbackPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    
    val sharedTransitionScope = LocalSharedTransitionScope.current
    
    if (currentMediaItem == null) return

    val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dims.ScreenPadding, vertical = Dims.TinyPadding)
            .shadow(24.dp, RoundedCornerShape(Dims.CardRadius), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(Dims.CardRadius))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = Dims.SmallPadding, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageModifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(Dims.SmallRadius))

                Box {
                    AsyncImage(
                        model = currentMediaItem?.mediaMetadata?.artworkUri,
                        contentDescription = null,
                        modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                            with(sharedTransitionScope) {
                                imageModifier.sharedBounds(
                                    rememberSharedContentState(key = "artwork_${currentMediaItem?.mediaId}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            }
                        } else imageModifier,
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(Dims.SmallPadding))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentMediaItem?.mediaMetadata?.title?.toString() ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                IconButton(
                    onClick = {
                        if (isPlaying) viewModel.pause() else viewModel.resume()
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dims.CardRadius)
                    .padding(bottom = 8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

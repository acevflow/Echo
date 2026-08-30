package com.acevflow.echo.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.acevflow.echo.ui.MainViewModel
import com.acevflow.echo.ui.theme.Dims

@Composable
fun MiniPlayer(
    viewModel: MainViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.playbackPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    
    if (currentMediaItem == null) return

    val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dims.SmallPadding, vertical = Dims.TinyPadding)
            .clip(RoundedCornerShape(Dims.CardRadius))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 12.dp,
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = Dims.SmallPadding, vertical = Dims.SmallPadding)
                    .fillMaxWidth()
                    .height(Dims.MiniPlayerHeight - 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentMediaItem?.mediaMetadata?.artworkUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(Dims.SmallRadius)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(Dims.ElementPadding))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentMediaItem?.mediaMetadata?.title?.toString() ?: "Unknown",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                IconButton(
                    onClick = {
                        if (isPlaying) viewModel.pause() else viewModel.resume()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(Dims.IconMedium),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

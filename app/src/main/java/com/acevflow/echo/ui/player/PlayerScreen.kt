package com.acevflow.echo.ui.player

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acevflow.echo.domain.util.TimeFormatter
import com.acevflow.echo.ui.MainViewModel
import com.acevflow.echo.ui.navigation.LocalNavAnimatedVisibilityScope
import com.acevflow.echo.ui.navigation.LocalSharedTransitionScope
import com.acevflow.echo.ui.queue.QueueSheet
import com.acevflow.echo.ui.queue.QueueViewModel
import com.acevflow.echo.ui.theme.Dims
import com.acevflow.echo.ui.theme.FavoriteRed

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackPosition by viewModel.playbackPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val shuffleModeEnabled by viewModel.shuffleModeEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val sleepTimerMillis by mainViewModel.sleepTimerMillisLeft.collectAsState()

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current

    var showQueueSheet by remember { mutableStateOf(false) }
    var showTimerMenu by remember { mutableStateOf(false) }

    if (currentMediaItem == null) return

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dims.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar / Status Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dims.ElementPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showQueueSheet = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = "Queue",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "NOW PLAYING",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(Dims.IconMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Artwork
            val imageModifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .shadow(16.dp, RoundedCornerShape(Dims.CardRadius))
                .clip(RoundedCornerShape(Dims.CardRadius))

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

            Spacer(modifier = Modifier.weight(1f))

            // Metadata & Favorite
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dims.SmallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentMediaItem?.mediaMetadata?.title?.toString() ?: "Unknown",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) FavoriteRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dims.IconMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dims.ScreenPadding))

            // Progress Slider
            var sliderPosition by remember(playbackPosition) {
                mutableFloatStateOf(playbackPosition.toFloat())
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Dims.SmallPadding)) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        viewModel.seekTo(sliderPosition.toLong())
                    },
                    valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = TimeFormatter.formatMs(playbackPosition),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = TimeFormatter.formatMs(duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dims.ElementPadding))

            // Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (shuffleModeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { viewModel.skipToPrevious() }) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            modifier = Modifier.size(Dims.IconLarge)
                        )
                    }

                    Spacer(modifier = Modifier.width(Dims.ElementPadding))

                    IconButton(
                        onClick = {
                            if (isPlaying) viewModel.pause() else viewModel.play()
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(Dims.ElementPadding))

                    IconButton(onClick = { viewModel.skipToNext() }) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            modifier = Modifier.size(Dims.IconLarge)
                        )
                    }
                }

                Box {
                    IconButton(onClick = { showTimerMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Sleep Timer",
                            tint = if (sleepTimerMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showTimerMenu,
                        onDismissRequest = { showTimerMenu = false }
                    ) {
                        if (sleepTimerMillis != null) {
                            DropdownMenuItem(
                                text = { Text("Cancel Timer (${TimeFormatter.formatMs(sleepTimerMillis!!)})") },
                                onClick = {
                                    mainViewModel.cancelSleepTimer()
                                    showTimerMenu = false
                                }
                            )
                        }
                        listOf(15, 30, 45, 60).forEach { mins ->
                            DropdownMenuItem(
                                text = { Text("$mins minutes") },
                                onClick = {
                                    mainViewModel.startSleepTimer(mins)
                                    showTimerMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            IconButton(onClick = { viewModel.toggleRepeatMode() }) {
                Icon(
                    imageVector = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showQueueSheet) {
        QueueSheet(
            viewModel = hiltViewModel(),
            onDismiss = { showQueueSheet = false }
        )
    }
}

package com.acevflow.echo.ui.player

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Lyrics
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val lyrics by viewModel.lyrics.collectAsState()

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current

    var showQueueSheet by remember { mutableStateOf(false) }
    var showTimerMenu by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }

    if (currentMediaItem == null) return

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .padding(horizontal = Dims.ScreenPadding, vertical = Dims.ElementPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar / Status Area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dims.SmallPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize",
                            modifier = Modifier.size(Dims.IconMedium)
                        )
                    }
                    
                    Text(
                        text = "NOW PLAYING",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        letterSpacing = 2.sp
                    )

                    IconButton(onClick = { showQueueSheet = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Queue"
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Artwork or Lyrics
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                        .aspectRatio(1f)
                        .shadow(64.dp, RoundedCornerShape(Dims.CardRadius), spotColor = Color.Black.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(Dims.CardRadius))
                        .clickable { showLyrics = !showLyrics }
                ) {
                    if (showLyrics) {
                        LyricsView(
                            lyrics = lyrics,
                            currentPosition = playbackPosition,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = currentMediaItem?.mediaMetadata?.artworkUri,
                            contentDescription = null,
                            modifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                with(sharedTransitionScope) {
                                    Modifier
                                        .fillMaxSize()
                                        .sharedBounds(
                                            rememberSharedContentState(key = "artwork_${currentMediaItem?.mediaId}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                }
                            } else {
                                Modifier.fillMaxSize()
                            },
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.7f))

                // Metadata & Favorite
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentMediaItem?.mediaMetadata?.title?.toString() ?: "Unknown",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 30.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) FavoriteRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress Slider
                var isDragging by remember { mutableStateOf(false) }
                var sliderPosition by remember(playbackPosition) {
                    mutableFloatStateOf(playbackPosition.toFloat())
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = if (isDragging) sliderPosition else playbackPosition.toFloat(),
                        onValueChange = { 
                            isDragging = true
                            sliderPosition = it 
                        },
                        onValueChangeFinished = {
                            isDragging = false
                            viewModel.seekTo(sliderPosition.toLong())
                        },
                        valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = TimeFormatter.formatMs(playbackPosition),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = TimeFormatter.formatMs(duration),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                            tint = if (shuffleModeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(24.dp)
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
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Surface(
                            modifier = Modifier
                                .size(88.dp)
                                .shadow(32.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                .clip(CircleShape)
                                .clickable {
                                    if (isPlaying) viewModel.pause() else viewModel.play()
                                },
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    modifier = Modifier.size(44.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        IconButton(onClick = { viewModel.skipToNext() }) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.toggleRepeatMode() }) {
                        Icon(
                            imageVector = when (repeatMode) {
                                Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (repeatMode != Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        IconButton(onClick = { showTimerMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Sleep Timer",
                                tint = if (sleepTimerMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                    
                    IconButton(onClick = { showLyrics = !showLyrics }) {
                        Icon(
                            imageVector = Icons.Default.Lyrics,
                            contentDescription = "Show Lyrics",
                            modifier = Modifier.size(Dims.IconSmall),
                            tint = if (showLyrics) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
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

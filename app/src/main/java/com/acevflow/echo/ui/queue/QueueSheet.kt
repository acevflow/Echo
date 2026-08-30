package com.acevflow.echo.ui.queue

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import com.acevflow.echo.ui.theme.Dims

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueSheet(
    viewModel: QueueViewModel,
    onDismiss: () -> Unit
) {
    val playlist by viewModel.playlist.collectAsState()
    val currentItem by viewModel.currentMediaItem.collectAsState()
    
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        viewModel.moveItem(from.index, to.index)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.8f),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Dims.ElementPadding)) {
            Text(
                text = "Upcoming Queue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Dims.ElementPadding)
            )

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(playlist, key = { index, item -> "${item.mediaId}_$index" }) { index, item ->
                    val isPlaying = item.mediaId == currentItem?.mediaId
                    
                    ReorderableItem(reorderableState, key = "${item.mediaId}_$index") { isDragging ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    alpha = if (isDragging) 0.8f else 1f
                                    scaleX = if (isDragging) 1.02f else 1f
                                    scaleY = if (isDragging) 1.02f else 1f
                                },
                            tonalElevation = if (isDragging) 8.dp else 0.dp,
                            color = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dims.TinyPadding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier.draggableHandle(),
                                    onClick = { }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Reorder",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.mediaMetadata.title?.toString() ?: "Unknown",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.mediaMetadata.artist?.toString() ?: "Unknown Artist",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                
                                IconButton(onClick = { viewModel.removeFromQueue(index) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

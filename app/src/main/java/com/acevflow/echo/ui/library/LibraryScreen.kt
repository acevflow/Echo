package com.acevflow.echo.ui.library

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acevflow.echo.domain.model.Playlist
import com.acevflow.echo.domain.model.Song
import com.acevflow.echo.ui.components.EchoSongItem
import com.acevflow.echo.ui.editor.BatchEditViewModel
import com.acevflow.echo.ui.editor.BatchEditUiState
import com.acevflow.echo.ui.theme.Dims

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val playlists by viewModel.playlists.collectAsState(initial = emptyList())
    val selectedIds by viewModel.selectedSongIds.collectAsState()
    
    var songForPlaylist by remember { mutableStateOf<Song?>(null) }
    var isBatchAddingToPlaylist by remember { mutableStateOf(false) }
    var showBatchEditDialog by remember { mutableStateOf(false) }
    
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadSongs()
        }
    }
    
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permission)
    }

    if (selectedIds.isNotEmpty()) {
        BackHandler {
            viewModel.clearSelection()
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                LibraryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                LibraryUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No music found",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Add audio files to your device",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is LibraryUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AnimatedVisibility(
                            visible = selectedIds.isNotEmpty(),
                            enter = slideInVertically { -it },
                            exit = slideOutVertically { -it }
                        ) {
                            SelectionActionBar(
                                selectedCount = selectedIds.size,
                                onClear = { viewModel.clearSelection() },
                                onAddToQueue = { viewModel.addSelectedToQueue(state.songs) },
                                onAddToPlaylist = { isBatchAddingToPlaylist = true },
                                onEditInfo = { showBatchEditDialog = true }
                            )
                        }

                        SongList(
                            viewModel = viewModel,
                            songs = state.songs,
                            selectedIds = selectedIds,
                            onSongClick = { index -> 
                                if (selectedIds.isNotEmpty()) {
                                    viewModel.toggleSelection(state.songs[index].id)
                                } else {
                                    viewModel.playSongs(state.songs, index)
                                }
                            },
                            onLongClick = { index ->
                                viewModel.toggleSelection(state.songs[index].id)
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
    }

    if (songForPlaylist != null || isBatchAddingToPlaylist) {
        val successState = uiState as? LibraryUiState.Success
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { 
                songForPlaylist = null
                isBatchAddingToPlaylist = false
            },
            onPlaylistSelected = { playlist ->
                if (isBatchAddingToPlaylist && successState != null) {
                    viewModel.addSelectedToPlaylist(playlist.id, successState.songs)
                } else {
                    songForPlaylist?.let { viewModel.addSongToPlaylist(playlist.id, it.id) }
                }
                songForPlaylist = null
                isBatchAddingToPlaylist = false
            }
        )
    }

    if (showBatchEditDialog) {
        BatchEditDialog(
            viewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
            selectedSongIds = selectedIds.toList(),
            onDismiss = { showBatchEditDialog = false },
            onSaved = {
                showBatchEditDialog = false
                viewModel.clearSelection()
            }
        )
    }
}

@Composable
fun BatchEditDialog(
    viewModel: BatchEditViewModel,
    selectedSongIds: List<Long>,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val artist by viewModel.artist.collectAsState()
    val album by viewModel.album.collectAsState()
    val pendingIntent by viewModel.pendingIntent.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setup(selectedSongIds)
    }

    LaunchedEffect(pendingIntent) {
        pendingIntent?.let {
            launcher.launch(androidx.activity.result.IntentSenderRequest.Builder(it.intentSender).build())
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is BatchEditUiState.Saved) {
            onSaved()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${selectedSongIds.size} Songs") },
        text = {
            Column {
                androidx.compose.material3.OutlinedTextField(
                    value = artist,
                    onValueChange = viewModel::onArtistChange,
                    label = { Text("Artist") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = album,
                    onValueChange = viewModel::onAlbumChange,
                    label = { Text("Album") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::saveChanges) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SelectionActionBar(
    selectedCount: Int,
    onClear: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onEditInfo: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
            }
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
            IconButton(onClick = onEditInfo) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Info")
            }
            IconButton(onClick = onAddToQueue) {
                Icon(Icons.Default.Queue, contentDescription = "Add to Queue")
            }
            IconButton(onClick = onAddToPlaylist) {
                Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = "Add to Playlist")
            }
        }
    }
}

@Composable
fun SongList(
    viewModel: LibraryViewModel,
    songs: List<Song>,
    selectedIds: Set<Long> = emptySet(),
    onSongClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit = {},
    onAddToPlaylist: (Song) -> Unit,
    onEditInfo: (Song) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dims.MiniPlayerHeight + Dims.ScreenPadding)
    ) {
        itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
            EchoSongItem(
                song = song,
                isSelected = selectedIds.contains(song.id),
                onClick = { onSongClick(index) },
                onLongClick = { onLongClick(index) },
                onPlayNext = { viewModel.playNext(song) },
                onAddToQueue = { viewModel.addToQueue(song) },
                onAddToPlaylist = { onAddToPlaylist(song) },
                onEditInfo = { onEditInfo(song) }
            )
            if (index < songs.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = Dims.ElementPadding),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddToPlaylistDialog(
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (Playlist) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Playlist") },
        text = {
            if (playlists.isEmpty()) {
                Text("No playlists found.")
            } else {
                LazyColumn {
                    itemsIndexed(playlists) { _, playlist ->
                        TextButton(
                            onClick = { onPlaylistSelected(playlist) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = playlist.name,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

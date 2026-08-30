package com.acevflow.echo.ui.library.folders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.acevflow.echo.domain.model.Folder
import com.acevflow.echo.ui.theme.Dims

@Composable
fun FoldersScreen(
    viewModel: FoldersViewModel,
    onFolderClick: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            FoldersUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            FoldersUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No folders found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is FoldersUiState.Success -> {
                FolderList(
                    folders = state.folders,
                    onFolderClick = onFolderClick
                )
            }
        }
    }
}

@Composable
fun FolderList(
    folders: List<Folder>,
    onFolderClick: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dims.MiniPlayerHeight + Dims.ScreenPadding)
    ) {
        itemsIndexed(folders, key = { _, folder -> folder.path }) { index, folder ->
            FolderItem(
                folder = folder,
                modifier = Modifier.clickable { onFolderClick(folder) }
            )
            if (index < folders.size - 1) {
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
fun FolderItem(
    folder: Folder,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dims.ElementPadding, vertical = Dims.ElementPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.medium),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dims.IconSmall)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dims.ElementPadding)
        ) {
            Text(
                text = folder.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${folder.trackCount} songs • ${folder.path}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

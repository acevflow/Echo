package com.acevflow.echo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.ui.theme.Dims

@Composable
fun EchoAlbumItem(
    album: Album,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dims.CardRadius)),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            AsyncImage(
                model = album.artworkUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(16.dp, RoundedCornerShape(Dims.CardRadius), spotColor = Color.Black.copy(alpha = 0.1f))
                    .clip(RoundedCornerShape(Dims.CardRadius)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EchoArtistItem(
    artist: Artist,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dims.ScreenPadding, vertical = 2.dp)
            .clip(RoundedCornerShape(Dims.SmallRadius)),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = Dims.TinyPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(Dims.ArtworkSmall)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dims.ElementPadding)
            ) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${artist.trackCount} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

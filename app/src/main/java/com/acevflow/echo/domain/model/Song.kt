package com.acevflow.echo.domain.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val contentUri: Uri,
    val artworkUri: Uri?,
    val isFavorite: Boolean = false,
    val parentPath: String? = null
)

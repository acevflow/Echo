package com.acevflow.echo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artwork_overrides")
data class ArtworkOverride(
    @PrimaryKey val songId: Long,
    val artworkUri: String
)

package com.acevflow.echo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acevflow.echo.data.local.dao.FavoriteSongDao
import com.acevflow.echo.data.local.dao.PlaybackHistoryDao
import com.acevflow.echo.data.local.dao.PlaylistDao
import com.acevflow.echo.data.local.dao.SearchHistoryDao
import com.acevflow.echo.data.local.dao.ArtworkOverrideDao
import com.acevflow.echo.data.local.entity.FavoriteSong
import com.acevflow.echo.data.local.entity.PlaybackHistory
import com.acevflow.echo.data.local.entity.Playlist
import com.acevflow.echo.data.local.entity.PlaylistSongCrossRef
import com.acevflow.echo.data.local.entity.SearchHistory
import com.acevflow.echo.data.local.entity.ArtworkOverride

/**
 * Room database for Echo.
 * Stores favorites, playlists, playback history, search queries, and artwork overrides.
 */
@Database(
    entities = [
        FavoriteSong::class,
        Playlist::class,
        PlaylistSongCrossRef::class,
        PlaybackHistory::class,
        SearchHistory::class,
        ArtworkOverride::class
    ],
    version = 5,
    exportSchema = false
)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun favoriteSongDao(): FavoriteSongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playbackHistoryDao(): PlaybackHistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun artworkOverrideDao(): ArtworkOverrideDao

    companion object {
        const val DATABASE_NAME = "echo_db"
    }
}

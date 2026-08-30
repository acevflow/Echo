package com.acevflow.echo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acevflow.echo.data.local.dao.FavoriteSongDao
import com.acevflow.echo.data.local.dao.PlaylistDao
import com.acevflow.echo.data.local.entity.FavoriteSong
import com.acevflow.echo.data.local.entity.Playlist
import com.acevflow.echo.data.local.entity.PlaylistSongCrossRef

@Database(
    entities = [
        FavoriteSong::class,
        Playlist::class,
        PlaylistSongCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun favoriteSongDao(): FavoriteSongDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        const val DATABASE_NAME = "echo_db"
    }
}

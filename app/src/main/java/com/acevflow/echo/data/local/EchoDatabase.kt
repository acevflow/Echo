package com.acevflow.echo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acevflow.echo.data.local.dao.FavoriteSongDao
import com.acevflow.echo.data.local.entity.FavoriteSong

@Database(entities = [FavoriteSong::class], version = 1, exportSchema = false)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun favoriteSongDao(): FavoriteSongDao

    companion object {
        const val DATABASE_NAME = "echo_db"
    }
}

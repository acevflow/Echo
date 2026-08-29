package com.acevflow.echo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acevflow.echo.data.local.entity.FavoriteSong
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSongDao {
    @Query("SELECT * FROM favorite_songs")
    fun getAllFavorites(): Flow<List<FavoriteSong>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId = :songId)")
    fun isFavorite(songId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteSong: FavoriteSong)

    @Delete
    suspend fun deleteFavorite(favoriteSong: FavoriteSong)

    @Query("DELETE FROM favorite_songs WHERE songId = :songId")
    suspend fun deleteFavoriteById(songId: Long)
}

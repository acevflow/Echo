package com.acevflow.echo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acevflow.echo.data.local.entity.ArtworkOverride
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtworkOverrideDao {
    @Query("SELECT * FROM artwork_overrides")
    fun getAllOverrides(): Flow<List<ArtworkOverride>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOverride(override: ArtworkOverride)

    @Query("DELETE FROM artwork_overrides WHERE songId = :songId")
    suspend fun deleteOverride(songId: Long)
}

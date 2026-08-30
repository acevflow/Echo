package com.acevflow.echo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acevflow.echo.data.local.entity.PlaybackHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackHistoryDao {
    @Query("SELECT * FROM playback_history ORDER BY playedAt DESC LIMIT 100")
    fun getRecentHistory(): Flow<List<PlaybackHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryEntry(history: PlaybackHistory)

    @Query("DELETE FROM playback_history")
    suspend fun clearHistory()
    
    @Query("DELETE FROM playback_history WHERE songId = :songId")
    suspend fun deleteHistoryBySongId(songId: Long)
}

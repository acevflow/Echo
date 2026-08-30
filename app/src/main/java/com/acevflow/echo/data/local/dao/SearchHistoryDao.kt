package com.acevflow.echo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acevflow.echo.data.local.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing search query history.
 */
@Dao
interface SearchHistoryDao {
    /** Returns a flow of the most recent search queries. */
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSearchHistory(): Flow<List<SearchHistory>>

    /** Inserts or updates a search query in the history. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(searchHistory: SearchHistory)

    /** Deletes a specific search query from the history. */
    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearchQuery(query: String)

    /** Clears all search history. */
    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
}

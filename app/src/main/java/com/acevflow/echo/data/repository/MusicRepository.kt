package com.acevflow.echo.data.repository

import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.domain.model.Folder
import com.acevflow.echo.domain.model.Playlist
import com.acevflow.echo.domain.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Core repository interface for music data management.
 * Provides streams for songs, albums, artists, folders, and playlists,
 * as well as methods for handling favorites, playback history, and smart collections.
 */
interface MusicRepository {
    /** Returns a flow of all music tracks on the device. */
    fun getSongs(): Flow<List<Song>>

    /** Returns a flow of all albums on the device. */
    fun getAlbums(): Flow<List<Album>>

    /** Returns a flow of all artists on the device. */
    fun getArtists(): Flow<List<Artist>>

    /** Returns a flow of physical folders containing music files. */
    fun getFolders(): Flow<List<Folder>>

    /** Returns a flow of songs belonging to a specific album. */
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>

    /** Returns a flow of albums associated with a specific artist name. */
    fun getAlbumsByArtist(artistName: String): Flow<List<Album>>

    /** Returns a flow of songs within a physical directory path. */
    fun getSongsByFolder(folderPath: String): Flow<List<Song>>

    /** Returns a flow of the favorite status for a specific song ID. */
    fun isFavorite(songId: Long): Flow<Boolean>

    /** Toggles the favorite status of a song. */
    suspend fun toggleFavorite(songId: Long)

    // Playlists
    /** Returns a flow of all user-created playlists. */
    fun getPlaylists(): Flow<List<Playlist>>

    /** Returns a flow of songs within a specific playlist. */
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>

    /** Creates a new playlist with the given name and returns its ID. */
    suspend fun createPlaylist(name: String): Long

    /** Deletes a playlist by its ID. */
    suspend fun deletePlaylist(playlistId: Long)

    /** Adds a song to a playlist. */
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long)

    /** Removes a song from a specific playlist. */
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    // History
    /** Returns a flow of recently played songs. */
    fun getRecentHistory(): Flow<List<Song>>

    /** Adds a song to the playback history. */
    suspend fun addSongToHistory(songId: Long)

    /** Clears all playback history entries. */
    suspend fun clearHistory()

    // Smart Collections
    /** Returns a flow of the most recently added songs on the device. */
    fun getRecentlyAdded(): Flow<List<Song>>

    /** Returns a flow of the most frequently played songs. */
    fun getMostPlayed(): Flow<List<Song>>

    // Search History
    /** Returns a flow of recent search queries. */
    fun getRecentSearchHistory(): Flow<List<String>>

    /** Saves a search query to history. */
    suspend fun addSearchQuery(query: String)

    /** Deletes a specific search query from history. */
    suspend fun deleteSearchQuery(query: String)

    /** Clears all search history. */
    suspend fun clearSearchHistory()
}

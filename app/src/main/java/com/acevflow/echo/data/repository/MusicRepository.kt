package com.acevflow.echo.data.repository

import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.domain.model.Folder
import com.acevflow.echo.domain.model.Playlist
import com.acevflow.echo.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getSongs(): Flow<List<Song>>
    fun getAlbums(): Flow<List<Album>>
    fun getArtists(): Flow<List<Artist>>
    fun getFolders(): Flow<List<Folder>>
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
    fun getAlbumsByArtist(artistName: String): Flow<List<Album>>
    fun getSongsByFolder(folderPath: String): Flow<List<Song>>
    fun isFavorite(songId: Long): Flow<Boolean>
    suspend fun toggleFavorite(songId: Long)

    // Playlists
    fun getPlaylists(): Flow<List<Playlist>>
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    // History
    fun getRecentHistory(): Flow<List<Song>>
    suspend fun addSongToHistory(songId: Long)
    suspend fun clearHistory()

    // Smart Collections
    fun getRecentlyAdded(): Flow<List<Song>>
    fun getMostPlayed(): Flow<List<Song>>
}

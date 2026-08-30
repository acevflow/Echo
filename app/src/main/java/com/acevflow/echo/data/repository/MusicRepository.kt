package com.acevflow.echo.data.repository

import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
import com.acevflow.echo.domain.model.Playlist
import com.acevflow.echo.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getSongs(): Flow<List<Song>>
    fun getAlbums(): Flow<List<Album>>
    fun getArtists(): Flow<List<Artist>>
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
    fun getAlbumsByArtist(artistName: String): Flow<List<Album>>
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
}

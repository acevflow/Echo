package com.acevflow.echo.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.acevflow.echo.data.local.dao.FavoriteSongDao
import com.acevflow.echo.data.local.entity.FavoriteSong
import com.acevflow.echo.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MediaStoreMusicRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteSongDao: FavoriteSongDao
) : MusicRepository {

    override fun getSongs(): Flow<List<Song>> = combine(
        getMediaStoreSongs(),
        favoriteSongDao.getAllFavorites()
    ) { mediaStoreSongs, favorites ->
        val favoriteIds = favorites.map { it.songId }.toSet()
        mediaStoreSongs.map { song ->
            song.copy(isFavorite = favoriteIds.contains(song.id))
        }
    }.flowOn(Dispatchers.IO)

    private fun getMediaStoreSongs(): Flow<List<Song>> = flow {
        val songs = mutableListOf<Song>()
        
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Unknown Title"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val album = cursor.getString(albumColumn) ?: "Unknown Album"
                val duration = cursor.getLong(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)
                
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                val artworkUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                
                songs.add(Song(id, title, artist, album, duration, contentUri, artworkUri))
            }
        }
        emit(songs)
    }

    override fun isFavorite(songId: Long): Flow<Boolean> = favoriteSongDao.isFavorite(songId)

    override suspend fun toggleFavorite(songId: Long) {
        val isFavorite = favoriteSongDao.isFavorite(songId).first()
        if (isFavorite) {
            favoriteSongDao.deleteFavoriteById(songId)
        } else {
            favoriteSongDao.insertFavorite(FavoriteSong(songId))
        }
    }
}

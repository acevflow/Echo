package com.acevflow.echo.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.acevflow.echo.data.local.dao.FavoriteSongDao
import com.acevflow.echo.data.local.entity.FavoriteSong
import com.acevflow.echo.domain.model.Album
import com.acevflow.echo.domain.model.Artist
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
        getMediaStoreSongs(null),
        favoriteSongDao.getAllFavorites()
    ) { mediaStoreSongs, favorites ->
        val favoriteIds = favorites.map { it.songId }.toSet()
        mediaStoreSongs.map { song ->
            song.copy(isFavorite = favoriteIds.contains(song.id))
        }
    }.flowOn(Dispatchers.IO)

    override fun getAlbums(): Flow<List<Album>> = flow {
        val albums = mutableListOf<Album>()
        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )
        val sortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"

        context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val numSongsCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val artworkUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    id
                )
                albums.add(
                    Album(
                        id = id,
                        title = cursor.getString(albumCol),
                        artist = cursor.getString(artistCol),
                        artworkUri = artworkUri,
                        trackCount = cursor.getInt(numSongsCol)
                    )
                )
            }
        }
        emit(albums)
    }.flowOn(Dispatchers.IO)

    override fun getArtists(): Flow<List<Artist>> = flow {
        val artists = mutableListOf<Artist>()
        val collection = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        )
        val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"

        context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val numTracksCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
            val numAlbumsCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)

            while (cursor.moveToNext()) {
                artists.add(
                    Artist(
                        id = cursor.getLong(idCol),
                        name = cursor.getString(artistCol),
                        trackCount = cursor.getInt(numTracksCol),
                        albumCount = cursor.getInt(numAlbumsCol)
                    )
                )
            }
        }
        emit(artists)
    }.flowOn(Dispatchers.IO)

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> = combine(
        getMediaStoreSongs("${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf(albumId.toString())),
        favoriteSongDao.getAllFavorites()
    ) { mediaStoreSongs, favorites ->
        val favoriteIds = favorites.map { it.songId }.toSet()
        mediaStoreSongs.map { song ->
            song.copy(isFavorite = favoriteIds.contains(song.id))
        }
    }.flowOn(Dispatchers.IO)

    override fun getAlbumsByArtist(artistName: String): Flow<List<Album>> = flow {
        val albums = mutableListOf<Album>()
        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )
        val selection = "${MediaStore.Audio.Albums.ARTIST} = ?"
        val selectionArgs = arrayOf(artistName)
        val sortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"

        context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val numSongsCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val artworkUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    id
                )
                albums.add(
                    Album(
                        id = id,
                        title = cursor.getString(albumCol),
                        artist = cursor.getString(artistCol),
                        artworkUri = artworkUri,
                        trackCount = cursor.getInt(numSongsCol)
                    )
                )
            }
        }
        emit(albums)
    }.flowOn(Dispatchers.IO)

    private fun getMediaStoreSongs(selection: String?, selectionArgs: Array<String>? = null): Flow<List<Song>> = flow {
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
        
        val finalSelection = if (selection != null) {
            "(${MediaStore.Audio.Media.IS_MUSIC} != 0) AND ($selection)"
        } else {
            "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        }
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
        context.contentResolver.query(
            collection,
            projection,
            finalSelection,
            selectionArgs,
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
    }.flowOn(Dispatchers.IO)

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

package com.acevflow.echo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.acevflow.echo.data.local.EchoDatabase
import com.acevflow.echo.data.local.dao.FavoriteSongDao
import com.acevflow.echo.data.local.dao.PlaylistDao
import com.acevflow.echo.data.repository.MediaStoreMusicRepository
import com.acevflow.echo.data.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        mediaStoreMusicRepository: MediaStoreMusicRepository
    ): MusicRepository

    companion object {
        @Provides
        @Singleton
        fun provideEchoDatabase(@ApplicationContext context: Context): EchoDatabase {
            return Room.databaseBuilder(
                context,
                EchoDatabase::class.java,
                EchoDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
        }

        @Provides
        @Singleton
        fun provideFavoriteSongDao(database: EchoDatabase): FavoriteSongDao {
            return database.favoriteSongDao()
        }

        @Provides
        @Singleton
        fun providePlaylistDao(database: EchoDatabase): PlaylistDao {
            return database.playlistDao()
        }

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile("user_preferences") }
            )
        }
    }
}

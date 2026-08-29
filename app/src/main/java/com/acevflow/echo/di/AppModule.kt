package com.acevflow.echo.di

import com.acevflow.echo.data.repository.MediaStoreMusicRepository
import com.acevflow.echo.data.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}

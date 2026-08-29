package com.acevflow.echo.data.repository

import com.acevflow.echo.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getSongs(): Flow<List<Song>>
}

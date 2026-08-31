package com.acevflow.echo.domain.usecase

import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the most frequently played songs based on local history.
 */
class GetMostPlayedUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(): Flow<List<Song>> = repository.getMostPlayed()
}

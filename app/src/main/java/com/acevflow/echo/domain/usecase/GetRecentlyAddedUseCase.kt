package com.acevflow.echo.domain.usecase

import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.domain.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the most recently added songs from the device.
 */
class GetRecentlyAddedUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(): Flow<List<Song>> = repository.getRecentlyAdded()
}

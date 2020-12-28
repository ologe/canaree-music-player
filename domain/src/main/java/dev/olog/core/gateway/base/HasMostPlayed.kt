package dev.olog.core.gateway.base

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Track
import kotlinx.coroutines.flow.Flow

interface HasMostPlayed {
    fun observeMostPlayed(mediaId: MediaId): Flow<List<Track>>
    suspend fun insertMostPlayed(mediaId: MediaId)
}
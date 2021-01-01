package dev.olog.domain.gateway.base

import dev.olog.domain.entity.track.Track
import dev.olog.domain.mediaid.MediaId
import kotlinx.coroutines.flow.Flow

interface HasMostPlayed {
    fun observeMostPlayed(mediaId: MediaId): Flow<List<Track>>
    suspend fun insertMostPlayed(mediaId: MediaId.Track)
}
package dev.olog.domain.gateway.base

import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface HasMostPlayed {
    fun observeMostPlayed(mediaId: MediaId.Category): Flow<List<Song>>
    suspend fun insertMostPlayed(mediaId: MediaId.Track)
}
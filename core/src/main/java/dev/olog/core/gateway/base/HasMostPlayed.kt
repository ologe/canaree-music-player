package dev.olog.core.gateway.base

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface HasMostPlayed {
    fun observeMostPlayed(mediaId: MediaId.Category): Flow<List<Song>>
    suspend fun insertMostPlayed(mediaId: MediaId.Track)
}
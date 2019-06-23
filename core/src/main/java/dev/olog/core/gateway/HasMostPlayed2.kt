package dev.olog.core.gateway

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface HasMostPlayed2 {
    fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>>
    suspend fun insertMostPlayed(mediaId: MediaId)
}
package dev.olog.core.gateway

import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface PlayingGateway {

    fun observe(): Flow<Song?>

    suspend fun update(id: String)

}
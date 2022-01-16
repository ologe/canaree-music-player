package dev.olog.core.gateway

import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import kotlinx.coroutines.flow.Flow

interface PlayingItemGateway {

    suspend fun set(uri: MediaUri)
    fun get(): Song? // TODO suspend? check performance
    fun observe(): Flow<Song?>

}
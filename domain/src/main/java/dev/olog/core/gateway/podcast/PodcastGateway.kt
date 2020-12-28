package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.Id
import kotlin.time.Duration

interface PodcastGateway :
    BaseGateway<Track, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(podcastList: List<Track>)

    suspend fun getCurrentPosition(podcastId: Long, duration: Duration): Duration
    suspend fun saveCurrentPosition(podcastId: Long, position: Duration)

    suspend fun getByAlbumId(albumId: Id): Track?
}
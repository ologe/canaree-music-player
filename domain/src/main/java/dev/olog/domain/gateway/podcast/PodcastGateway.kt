package dev.olog.domain.gateway.podcast

import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.base.BaseGateway
import dev.olog.domain.gateway.base.Id
import kotlin.time.Duration

interface PodcastGateway :
    BaseGateway<Track, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(podcastList: List<Track>)

    suspend fun getCurrentPosition(podcastId: Long, duration: Duration): Duration
    suspend fun saveCurrentPosition(podcastId: Long, position: Duration)

    suspend fun getByAlbumId(albumId: Id): Track?
}
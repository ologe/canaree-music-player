package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.gateway.base.Id

interface PodcastGateway :
    BaseGateway<Song, Id> {

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(podcastList: List<Song>)

    suspend fun getCurrentPosition(podcastId: Long, duration: Long): Long
    suspend fun saveCurrentPosition(podcastId: Long, position: Long)

    fun getByAlbumId(albumId: Id): Song?
}
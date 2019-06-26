package dev.olog.core.gateway

import dev.olog.core.entity.track.Song
import io.reactivex.Completable

interface PodcastGateway : BaseGateway<Song, Id> {

    fun deleteSingle(id: Id): Completable
    fun deleteGroup(podcastList: List<Song>): Completable

    fun getCurrentPosition(podcastId: Long, duration: Long): Long
    fun saveCurrentPosition(podcastId: Long, position: Long)

    fun getByAlbumId(albumId: Id): Song?
}
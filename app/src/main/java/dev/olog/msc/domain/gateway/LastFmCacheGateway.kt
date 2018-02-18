package dev.olog.msc.domain.gateway

import dev.olog.msc.api.last.fm.model.SearchedImage
import dev.olog.msc.api.last.fm.model.SearchedTrack
import io.reactivex.Completable
import io.reactivex.Single

interface LastFmCacheGateway {

    fun getTrack(songId: Long): Single<SearchedTrack>
    fun getTrackImage(songId: Long): Single<SearchedImage>

    fun insertTrack(info: SearchedTrack): Completable
    fun insertTrackImage(image: SearchedImage): Completable

}
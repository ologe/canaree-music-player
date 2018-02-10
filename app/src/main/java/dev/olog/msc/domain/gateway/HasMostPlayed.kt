package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.Flowable

interface HasMostPlayed {

    fun getMostPlayed(mediaId: MediaId): Flowable<List<Song>>
    fun insertMostPlayed(mediaId: MediaId): Completable

}
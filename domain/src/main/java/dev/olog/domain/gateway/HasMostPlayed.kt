package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import dev.olog.shared.MediaId
import io.reactivex.Completable
import io.reactivex.Flowable

interface HasMostPlayed {

    fun getMostPlayed(mediaId: MediaId): Flowable<List<Song>>
    fun insertMostPlayed(mediaId: MediaId): Completable

}
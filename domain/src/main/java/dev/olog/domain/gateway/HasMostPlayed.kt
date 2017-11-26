package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable

interface HasMostPlayed<in Param> {

    fun getMostPlayed(param: Param): Flowable<List<Song>>
    fun insertMostPlayed(mediaId: String): Completable

}
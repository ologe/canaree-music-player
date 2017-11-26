package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Flowable

interface HasMostPlayed<Param> {

    fun getMostPlayed(param: Param): Flowable<List<Song>>

}
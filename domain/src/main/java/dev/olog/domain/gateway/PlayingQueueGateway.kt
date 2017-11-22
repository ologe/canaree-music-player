package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable

interface PlayingQueueGateway {

    fun getAll(): Flowable<List<Song>>

    fun update(list: List<Long>): Completable

}
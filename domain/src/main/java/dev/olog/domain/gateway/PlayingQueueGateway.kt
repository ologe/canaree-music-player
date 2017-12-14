package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface PlayingQueueGateway {

    fun observeAll(): Flowable<List<Song>>

    fun getAll(): Single<List<Song>>

    fun update(list: List<Pair<String, Long>>): Completable

    fun observeMiniQueue() : Flowable<List<Song>>
    fun updateMiniQueue(data: List<Long>)

}
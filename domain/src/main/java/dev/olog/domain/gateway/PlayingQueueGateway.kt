package dev.olog.domain.gateway

import dev.olog.domain.entity.PlayingQueueSong
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface PlayingQueueGateway {

    fun observeAll(): Flowable<List<PlayingQueueSong>>

    fun getAll(): Single<List<PlayingQueueSong>>

    fun update(list: List<Pair<String, Long>>): Completable

    fun observeMiniQueue() : Flowable<List<PlayingQueueSong>>
    fun updateMiniQueue(data: List<Long>)

}
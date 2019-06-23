package dev.olog.core.gateway

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PlayingQueueGateway {

    fun observeAll(): Observable<List<PlayingQueueSong>>

    fun getAll(): Single<List<PlayingQueueSong>>

    fun update(list: List<UpdatePlayingQueueUseCaseRequest>): Completable

    fun observeMiniQueue(): Observable<List<PlayingQueueSong>>
    fun updateMiniQueue(tracksId: List<Pair<Int, Long>>)

}
package dev.olog.core.gateway

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    fun observeAll(): Observable<List<PlayingQueueSong>>

    fun getAll(): Single<List<PlayingQueueSong>>

    fun update(list: List<UpdatePlayingQueueUseCaseRequest>): Completable

}
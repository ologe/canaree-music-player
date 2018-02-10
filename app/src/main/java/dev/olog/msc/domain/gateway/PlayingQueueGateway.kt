package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.interactor.music.service.UpdatePlayingQueueUseCaseRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface PlayingQueueGateway {

    fun observeAll(): Flowable<List<PlayingQueueSong>>

    fun getAll(): Single<List<PlayingQueueSong>>

    fun update(list: List<UpdatePlayingQueueUseCaseRequest>): Completable

    fun observeMiniQueue() : Flowable<List<PlayingQueueSong>>
    fun updateMiniQueue(data: List<PlayingQueueSong>)

}
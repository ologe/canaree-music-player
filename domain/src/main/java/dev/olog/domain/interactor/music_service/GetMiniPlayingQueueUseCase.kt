package dev.olog.domain.interactor.music_service

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetMiniPlayingQueueUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val playingQueueGateway: PlayingQueueGateway

) : FlowableUseCase<List<PlayingQueueSong>>(schedulers) {

    override fun buildUseCaseObservable(): Flowable<List<PlayingQueueSong>> {
        return playingQueueGateway.observeMiniQueue()
    }



}
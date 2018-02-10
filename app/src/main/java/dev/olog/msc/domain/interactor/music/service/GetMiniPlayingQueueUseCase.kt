package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlayingQueueGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
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
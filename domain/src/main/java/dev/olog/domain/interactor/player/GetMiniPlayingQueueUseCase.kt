package dev.olog.domain.interactor.player

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetMiniPlayingQueueUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val playingQueueGateway: PlayingQueueGateway

) : FlowableUseCase<List<Song>>(schedulers) {


    override fun buildUseCaseObservable(): Flowable<List<Song>> {
        return playingQueueGateway.observeMiniQueue()
    }
}
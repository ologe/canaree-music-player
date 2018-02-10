package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlayingQueueGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class ObservePlayingQueueUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlayingQueueGateway

) : FlowableUseCase<List<PlayingQueueSong>>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<List<PlayingQueueSong>> {
        return gateway.observeAll()
    }
}
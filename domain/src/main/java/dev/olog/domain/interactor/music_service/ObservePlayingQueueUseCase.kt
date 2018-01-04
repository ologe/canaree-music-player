package dev.olog.domain.interactor.music_service

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.interactor.base.FlowableUseCase
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
package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlayingQueueGateway
import dev.olog.msc.domain.interactor.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetPlayingQueueUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlayingQueueGateway

) : SingleUseCase<List<PlayingQueueSong>>(scheduler) {

    override fun buildUseCaseObservable(): Single<List<PlayingQueueSong>> = gateway.getAll()
}
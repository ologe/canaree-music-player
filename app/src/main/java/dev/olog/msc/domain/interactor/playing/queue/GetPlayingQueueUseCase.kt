package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.interactor.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetPlayingQueueUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val gateway: PlayingQueueGateway

) : SingleUseCase<List<PlayingQueueSong>>(scheduler) {

    override fun buildUseCaseObservable(): Single<List<PlayingQueueSong>> = gateway.getAll()
}
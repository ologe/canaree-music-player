package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.msc.domain.interactor.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetMiniQueueUseCase @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: PlayingQueueGateway
) : SingleUseCase<List<PlayingQueueSong>>(schedulers){

    override fun buildUseCaseObservable(): Single<List<PlayingQueueSong>> {
        return gateway.observeMiniQueue()
                .firstOrError()
    }
}
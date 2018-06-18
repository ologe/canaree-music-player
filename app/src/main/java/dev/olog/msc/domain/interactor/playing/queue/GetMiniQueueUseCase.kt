package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlayingQueueGateway
import dev.olog.msc.domain.interactor.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetMiniQueueUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: PlayingQueueGateway
) : SingleUseCase<List<Song>>(schedulers){

    override fun buildUseCaseObservable(): Single<List<Song>> {
        return gateway.observeMiniQueue()
                .firstOrError()
    }
}
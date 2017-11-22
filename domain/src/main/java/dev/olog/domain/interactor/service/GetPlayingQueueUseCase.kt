package dev.olog.domain.interactor.service

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.interactor.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetPlayingQueueUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlayingQueueGateway

) : SingleUseCase<List<Song>>(scheduler) {

    override fun buildUseCaseObservable(): Single<List<Song>> {
        return gateway.getAll()
    }
}
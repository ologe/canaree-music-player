package dev.olog.domain.interactor.music_service

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.interactor.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetPlayingQueueUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlayingQueueGateway

) : SingleUseCase<List<PlayingQueueSong>>(scheduler) {

    override fun buildUseCaseObservable(): Single<List<PlayingQueueSong>> = gateway.getAll()
}
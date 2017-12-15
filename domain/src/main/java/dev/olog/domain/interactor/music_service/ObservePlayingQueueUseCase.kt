package dev.olog.domain.interactor.music_service

import dev.olog.domain.entity.Song
import dev.olog.domain.entity.toSong
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import dev.olog.shared.groupMap
import io.reactivex.Flowable
import javax.inject.Inject

class ObservePlayingQueueUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlayingQueueGateway

) : FlowableUseCase<List<Song>>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<List<Song>> {
        return gateway.observeAll()
                .groupMap { it.toSong() }
    }
}
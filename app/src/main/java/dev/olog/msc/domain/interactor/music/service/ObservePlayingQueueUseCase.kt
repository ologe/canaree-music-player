package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlayingQueueGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObservePlayingQueueUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlayingQueueGateway

) : ObservableUseCase<List<PlayingQueueSong>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<PlayingQueueSong>> {
        return gateway.observeAll()
    }
}
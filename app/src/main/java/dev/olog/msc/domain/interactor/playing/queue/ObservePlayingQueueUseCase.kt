package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.interactor.base.ObservableUseCase
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
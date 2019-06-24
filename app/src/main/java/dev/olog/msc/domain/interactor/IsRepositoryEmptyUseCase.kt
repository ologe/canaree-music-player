package dev.olog.msc.domain.interactor

import dev.olog.core.executor.ComputationScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.playing.queue.ObservePlayingQueueUseCase
import dev.olog.shared.extensions.debounceFirst
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class IsRepositoryEmptyUseCase @Inject constructor(
    scheduler: ComputationScheduler,
    private val playingQueueUseCase: ObservePlayingQueueUseCase

): ObservableUseCase<Boolean>(scheduler) {


    override fun buildUseCaseObservable(): Observable<Boolean> {
        return playingQueueUseCase.execute()
                .debounceFirst(500, TimeUnit.MILLISECONDS)
                .map { it.isEmpty() }
                .distinctUntilChanged()
    }
}
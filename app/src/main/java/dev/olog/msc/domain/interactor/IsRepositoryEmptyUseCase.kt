package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.playing.queue.ObservePlayingQueueUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class IsRepositoryEmptyUseCase @Inject constructor(
        scheduler: ComputationScheduler,
        private val allSongsUseCase: GetAllSongsUseCase,
        private val allPodcastUseCase: GetAllSongsUseCase,
        private val playingQueueUseCase: ObservePlayingQueueUseCase

): ObservableUseCase<Boolean>(scheduler) {


    override fun buildUseCaseObservable(): Observable<Boolean> {
        return Observables.combineLatest(
                allSongsUseCase.execute().debounce(500, TimeUnit.MILLISECONDS),
                allPodcastUseCase.execute().debounce(500, TimeUnit.MILLISECONDS),
                playingQueueUseCase.execute().debounce(500, TimeUnit.MILLISECONDS),
                { songs, podcasts, queue -> (songs.isEmpty() && podcasts.isEmpty()) || queue.isEmpty() }
        ).distinctUntilChanged()
    }
}
package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class IsRepositoryEmptyUseCase @Inject constructor(
        scheduler: ComputationScheduler,
        private val allSongsUseCase: GetAllSongsUseCase

): ObservableUseCase<Boolean>(scheduler) {


    override fun buildUseCaseObservable(): Observable<Boolean> {
        return allSongsUseCase.execute()
                .map { it.isEmpty() }
                .distinctUntilChanged()
    }
}
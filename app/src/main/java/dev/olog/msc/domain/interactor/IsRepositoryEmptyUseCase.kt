package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.base.FlowableUseCase
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class IsRepositoryEmptyUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val allSongsUseCase: GetAllSongsUseCase

): FlowableUseCase<Boolean>(scheduler) {


    override fun buildUseCaseObservable(): Flowable<Boolean> {
        return allSongsUseCase.execute().map { it.isEmpty() }
                .distinctUntilChanged()
    }
}
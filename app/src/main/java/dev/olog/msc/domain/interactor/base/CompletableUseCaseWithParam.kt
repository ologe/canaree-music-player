package dev.olog.msc.domain.interactor.base

import dev.olog.msc.domain.executors.Schedulers
import io.reactivex.Completable

abstract class CompletableUseCaseWithParam<in Param>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(param: Param): Completable

    fun execute(param: Param): Completable {
        return Completable.defer { this.buildUseCaseObservable(param)
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}
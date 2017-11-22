package dev.olog.domain.interactor.base

import dev.olog.domain.executor.Schedulers
import io.reactivex.Completable

abstract class CompletableUseCaseWithParam<in Param>(
        private val schedulers: Schedulers
) {

    internal abstract fun buildUseCaseObservable(param: Param): Completable

    fun execute(param: Param): Completable {
        return Completable.defer { this.buildUseCaseObservable(param)
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}
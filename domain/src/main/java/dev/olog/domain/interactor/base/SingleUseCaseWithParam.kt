package dev.olog.domain.interactor.base

import dev.olog.domain.executor.Schedulers
import io.reactivex.Single


abstract class SingleUseCaseWithParam<T, Param>(
        private val schedulers: Schedulers
) {

    internal abstract fun buildUseCaseObservable(param: Param): Single<T>

    fun execute(param: Param): Single<T> {
        return Single.defer { this.buildUseCaseObservable(param)
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
                .doOnError { it.printStackTrace() }
    }

}

package dev.olog.domain.interactor.base

import dev.olog.domain.executor.Schedulers
import io.reactivex.Single


abstract class SingleUseCase<T>(
        private val schedulers: Schedulers
) {

    internal abstract fun buildUseCaseObservable(): Single<T>

    fun execute(): Single<T> {
        return Single.defer { this.buildUseCaseObservable()
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}

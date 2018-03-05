package dev.olog.msc.domain.interactor.base

import dev.olog.msc.domain.executors.Schedulers
import io.reactivex.Single


abstract class SingleUseCase<T>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(): Single<T>

    fun execute(): Single<T> {
        return Single.defer { this.buildUseCaseObservable()
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}

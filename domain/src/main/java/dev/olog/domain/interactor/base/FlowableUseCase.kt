package dev.olog.domain.interactor.base

import dev.olog.domain.executor.Schedulers
import io.reactivex.Flowable

abstract class FlowableUseCase<T>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(): Flowable<T>

    fun execute(): Flowable<T> {
        return Flowable.defer { this.buildUseCaseObservable()
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}
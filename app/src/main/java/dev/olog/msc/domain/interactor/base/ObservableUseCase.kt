package dev.olog.msc.domain.interactor.base

import dev.olog.msc.domain.executors.Schedulers
import io.reactivex.Observable

abstract class ObservableUseCase<T>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(): Observable<T>

    fun execute(): Observable<T> {
        return Observable.defer {
            this.buildUseCaseObservable()
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}
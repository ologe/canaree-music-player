package dev.olog.msc.domain.interactor.base

import dev.olog.msc.domain.executors.Schedulers
import io.reactivex.Observable

abstract class ObservableUseCaseUseCaseWithParam<T, in Param>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(param: Param): Observable<T>

    fun execute(param: Param): Observable<T> {
        return Observable.defer { this.buildUseCaseObservable(param)
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
                .doOnError { it.printStackTrace() }
    }

}
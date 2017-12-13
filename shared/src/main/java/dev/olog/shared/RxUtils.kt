package dev.olog.shared

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

fun Disposable?.unsubscribe(){
    this?.let {
        if (!isDisposed){
            dispose()
        }
    }
}

fun <T, R> Flowable<List<T>>.groupMap(mapper: ((T) -> R)): Flowable<List<R>> {
    return flatMapSingle { Flowable.fromIterable(it).map(mapper).toList() }
}

fun <T, R> Single<List<T>>.groupMap(mapper: ((T) -> R)): Single<List<R>> {
    return flatMap { Flowable.fromIterable(it).map(mapper).toList() }
}
package dev.olog.msc.utils.k.extension

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

fun Disposable?.unsubscribe(){
    this?.let {
        if (!isDisposed){
            dispose()
        }
    }
}

fun <T> Flowable<T>.asLiveData() : LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(this)
}

fun <T> Observable<T>.asLiveData(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST)
        : LiveData<T> {

    return LiveDataReactiveStreams.fromPublisher(
            this.toFlowable(backpressureStrategy)
    )
}

fun <T, R> Flowable<List<T>>.groupMap(mapper: ((T) -> R)): Flowable<List<R>> {
    return flatMapSingle { Flowable.fromIterable(it).map(mapper).toList() }
}

fun <T, R> Single<List<T>>.groupMap(mapper: ((T) -> R)): Single<List<R>> {
    return flatMap { Flowable.fromIterable(it).map(mapper).toList() }
}

fun <T,R> Flowable<List<T>>.flatMapGroup(func : Flowable<T>.() -> Flowable<R>) : Flowable<List<R>> {
    return flatMapSingle { Flowable.fromIterable(it)
            .func()
            .toList()
    }
}

fun <T,R> Single<List<T>>.flatMapGroup(func : Flowable<T>.() -> Flowable<R>) : Single<List<R>> {
    return flatMap { Flowable.fromIterable(it)
            .func()
            .toList()
    }
}
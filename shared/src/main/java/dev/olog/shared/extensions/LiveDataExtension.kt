package dev.olog.shared.extensions

import androidx.lifecycle.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer {
        if (it != null){
            func(it)
        }
    })
}

fun <T> Flowable<T>.asLiveData() : LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(this)
}

fun <T> Observable<T>.asLiveData(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST)
        : LiveData<T> {

    return LiveDataReactiveStreams.fromPublisher(this.toFlowable(backpressureStrategy))
}

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return Transformations.distinctUntilChanged(this)
}

fun <T> LiveData<T>.filter(filter: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource<T>(this) { x ->
        if (filter(x)) {
            result.value = x
        }

    }
    return result
}

fun <T, R> LiveData<T>.map(function: (T) -> R): LiveData<R> {
    return Transformations.map(this) {
        function(it)
    }
}
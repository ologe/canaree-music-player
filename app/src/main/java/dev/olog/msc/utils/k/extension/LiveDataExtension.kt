package dev.olog.msc.utils.k.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, androidx.lifecycle.Observer {
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
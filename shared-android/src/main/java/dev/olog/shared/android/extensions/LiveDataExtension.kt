@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData as asAndroidXLiveData
import kotlinx.coroutines.flow.Flow


fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer {
        if (it != null) {
            func(it)
        }
    })
}

inline fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    val mediator = MediatorLiveData<T>()
    mediator.addSource(this) { value ->
        if (mediator.value != value) {
            mediator.value = value
        }
    }
    return mediator
}

inline fun <T> LiveData<T>.filter(crossinline filter: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource<T>(this) { x ->
        if (filter(x)) {
            result.value = x
        }

    }
    return result
}

inline fun <T, R> LiveData<T>.map(crossinline function: (T) -> R): LiveData<R> {
    val mediator = MediatorLiveData<R>()
    mediator.addSource(this) { value ->
        mediator.value = function(value)
    }
    return mediator
}

fun <T> Flow<T>.asLiveData(): LiveData<T> {
    return asAndroidXLiveData()
}
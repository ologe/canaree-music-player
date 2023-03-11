@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.EmptyCoroutineContext


fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer {
        if (it != null){
            func(it)
        }
    })
}

inline fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) { x ->
        if (result.value != x) {
            result.value = x
        }
    }
    return result
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
    return Transformations.map(this) {
        function(it)
    }
}

fun <T> Flow<T>.asLiveData(): LiveData<T> {
    return asLiveData(EmptyCoroutineContext)
}
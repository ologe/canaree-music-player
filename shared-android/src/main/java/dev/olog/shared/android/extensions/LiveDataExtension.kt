@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import androidx.lifecycle.*


fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer {
        if (it != null){
            func(it)
        }
    })
}

inline fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return this // todo
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
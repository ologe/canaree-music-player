@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import androidx.lifecycle.*
import dev.olog.shared.android.coroutine.autoDisposeJob
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext


fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer {
        if (it != null){
            func(it)
        }
    })
}

inline fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return Transformations.distinctUntilChanged(this)
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

class FlowLiveData<T>(
    private val flow: Flow<T>,
    private val context: CoroutineContext = Dispatchers.Unconfined
) : LiveData<T>() {

    private var job by autoDisposeJob()

    override fun onActive() {
        job = GlobalScope.launch(context) {
            flow.collect {
                if (it != null && it != value) {
                    withContext(Dispatchers.Main){
                        value = it
                    }
                }
            }
        }
    }

    override fun onInactive() {
        job = null
    }

}

fun <T> Flow<T>.asLiveData(context: CoroutineContext = Dispatchers.Unconfined): LiveData<T> {
    return FlowLiveData(this, context)
}
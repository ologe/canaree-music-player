package dev.olog.shared.android.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer


fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer {
        if (it != null) {
            func(it)
        }
    })
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

fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> LiveData<T1>.combine(
    liveData1: LiveData<T2>,
    liveData2: LiveData<T3>,
    liveData3: LiveData<T4>,
    liveData4: LiveData<T5>,
    combiner: (T1, T2, T3, T4, T5) -> R
): LiveData<R> {
    var value1: T1? = this.value
    var value2: T2? = liveData1.value
    var value3: T3? = liveData2.value
    var value4: T4? = liveData3.value
    var value5: T5? = liveData4.value

    val mediator = MediatorLiveData<R>()

    fun emit() {
        if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null) {
            mediator.value = combiner(value1!!, value2!!, value3!!, value4!!, value5!!)
        }
    }

    mediator.addSource(this) {
        value1 = it
        emit()
    }

    mediator.addSource(liveData1) {
        value2 = it
        emit()
    }
    mediator.addSource(liveData2) {
        value3 = it
        emit()
    }
    mediator.addSource(liveData3) {
        value4 = it
        emit()
    }
    mediator.addSource(liveData4) {
        value5 = it
        emit()
    }

    return mediator
}
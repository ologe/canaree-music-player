package dev.olog.data.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

fun <T> LiveData<T>.asFlow(): Flow<T> = channelFlow {
    runOnMainThread {
        value?.let {
            if (!isClosedForSend) {
                offer(it)
            }
        }
    }

    val observer = Observer<T> {
        runOnMainThread {
            it?.let {
                if (!isClosedForSend) {
                    offer(it)
                }
            }
        }
    }

    runOnMainThread { observeForever(observer) }

    invokeOnClose {
        runOnMainThread { removeObserver(observer) }
    }

}

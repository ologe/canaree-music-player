package dev.olog.data.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

fun <T> LiveData<T>.asFlow(): Flow<T> {

    val channel = ConflatedBroadcastChannel<T>()

    if (!channel.isClosedForSend && value != null){
        channel.offer(value!!)
    }

    val observer = Observer<T> {
        if (!channel.isClosedForSend && it != null){
            channel.offer(it)
        }
    }

    runOnMainThread {
        observeForever(observer)
    }

    channel.invokeOnClose {
        runOnMainThread { removeObserver(observer) }
    }

    return channel.openSubscription().consumeAsFlow()
}

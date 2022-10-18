@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.utils

import android.os.Looper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun <T> Flow<T>.assertBackground(): Flow<T> {
    return channelFlow {
        assertBackgroundThread()
        collect { trySend(it) }
    }
}

fun assertBackgroundThread() {
    if (isMainThread()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}
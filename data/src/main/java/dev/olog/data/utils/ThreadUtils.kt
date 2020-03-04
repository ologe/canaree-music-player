@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.utils

import android.os.Looper
import dev.olog.data.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun <T> Flow<T>.assertBackground(): Flow<T> {
    return channelFlow {
        assertBackgroundThread()
        collect { offer(it) }
    }
}

fun assertBackgroundThread() {
    if (BuildConfig.DEBUG && isMainThread()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}
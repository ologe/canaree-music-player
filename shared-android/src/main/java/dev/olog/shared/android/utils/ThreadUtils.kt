@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect


private val handler = Handler(Looper.getMainLooper())

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

inline fun assertMainThread() {
    if (!isMainThread()) {
        throw AssertionError("not on main thread, current=${Thread.currentThread()}")

    }
}

inline fun assertBackgroundThread() {
    if (isMainThread()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}

fun runOnMainThread(func: () -> Unit) {
    if (isMainThread()) {
        func()
    } else {
        handler.post(func)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.assertBackground(): Flow<T> {
    return channelFlow {
        assertBackgroundThread()
        collect { offer(it) }
    }
}
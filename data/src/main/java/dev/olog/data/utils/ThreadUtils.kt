@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.utils

import android.os.Looper
import dev.olog.data.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.assertBackground(): Flow<T> {
    return flow {
        assertBackgroundThread()
        collect { emit(it) }
    }
}

inline fun assertBackgroundThread() {
    if (BuildConfig.DEBUG && isMainThread()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}
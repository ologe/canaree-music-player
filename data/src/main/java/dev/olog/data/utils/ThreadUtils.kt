@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.utils

import android.os.Looper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect

//private val isTestMode by lazy {
//    try {
//        Class.forName("org.junit.Test")
//        true
//    } catch (ignored: Throwable) {
//        false
//    }
//}

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun <T> Flow<T>.assertBackground(): Flow<T> {
    return channelFlow {
        assertBackgroundThread()
        collect { offer(it) }
    }
}

fun assertBackgroundThread() {
    if (isMainThread()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}
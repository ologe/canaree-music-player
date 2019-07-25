@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.utils

import android.os.Handler
import android.os.Looper
import dev.olog.shared.BuildConfig

private val handler = Handler(Looper.getMainLooper())

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

inline fun assertMainThread() {
    if (BuildConfig.DEBUG && !isMainThread()) {
        throw AssertionError("not on main thread, current=${Thread.currentThread()}")

    }
}

inline fun assertBackgroundThread() {
    if (BuildConfig.DEBUG && isMainThread()) {
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
@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.utils

import android.os.Looper
import dev.olog.shared.android.BuildConfig

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

inline fun assertMainThread() {
    if (BuildConfig.DEBUG && !isMainThread()) {
        throw AssertionError("not on main thread, current=${Thread.currentThread()}")

    }
}

fun assertBackgroundThread() {
    if (BuildConfig.DEBUG && isMainThread()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}
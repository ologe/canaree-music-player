@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.utils

import android.os.Looper
import dev.olog.data.BuildConfig

//private val isTestMode by lazy {
//    try {
//        Class.forName("org.junit.Test")
//        true
//    } catch (ignored: Throwable) {
//        false
//    }
//}

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun assertBackgroundThread() {
    if (/*!isTestMode &&*/ BuildConfig.DEBUG && isMainThread()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}
package dev.olog.shared.android.utils

import android.os.Looper
import dev.olog.shared.android.BuildConfig

private fun isTesting(): Boolean {
    return try {
        Class.forName("org.junit.Test")
        true
    } catch (ex: ClassNotFoundException) {
        false
    }
}

fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun assertMainThread() {
    if (BuildConfig.DEBUG && !isMainThread() && !isTesting()) {
        throw AssertionError("not on main thread, current=${Thread.currentThread()}")
    }
}

fun assertBackgroundThread() {
    if (BuildConfig.DEBUG && isMainThread() && !isTesting()) {
        throw AssertionError("not on worker thread, current=${Thread.currentThread()}")
    }
}
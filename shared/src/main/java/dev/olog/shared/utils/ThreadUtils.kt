package dev.olog.shared.utils

import android.os.Handler
import android.os.Looper

private val handler = Handler(Looper.getMainLooper())
fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun assertMainThread() {
    if (BuildConfig.DEBUG) {
        val isMainThread = isMainThread()
        if (!isMainThread) {
            throw AssertionError("not on main thread " + Thread.currentThread())
        }
    }
}

fun assertBackgroundThread() {
    if (BuildConfig.DEBUG) {
        val isBackgroundThread = !isMainThread()
        if (!isBackgroundThread) {
            throw AssertionError("not on worker thread " + Thread.currentThread())
        }
    }
}

fun runOnMainThread(func : () -> Unit){
    if (isMainThread()){
        func()
    } else {
        handler.post(func)
    }
}
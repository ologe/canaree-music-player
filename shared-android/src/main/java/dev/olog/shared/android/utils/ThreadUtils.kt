package dev.olog.shared.android.utils

import android.os.Handler
import android.os.Looper

private val handler = Handler(Looper.getMainLooper())

fun runOnMainThread(func: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        func()
    } else {
        handler.post(func)
    }
}
@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.utils

import android.os.Handler
import android.os.Looper


private val handler = Handler(Looper.getMainLooper())

inline fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun runOnMainThread(func: () -> Unit) {
    if (isMainThread()) {
        func()
    } else {
        handler.post(func)
    }
}
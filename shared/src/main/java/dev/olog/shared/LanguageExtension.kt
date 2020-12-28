package dev.olog.shared

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

inline fun <T> lazyFast(crossinline operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}

inline val <T> T.exhaustive: T
    get() = this

@Suppress("NOTHING_TO_INLINE")
@Deprecated("fix")
inline fun <T> Continuation<T?>.safeResume(item: T?) {
    try {
        resume(item)
    } catch (ex: Throwable) {
        ex.printStackTrace()
        // already resumed
    }
}
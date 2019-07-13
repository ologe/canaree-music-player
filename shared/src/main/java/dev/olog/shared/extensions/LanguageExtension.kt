package dev.olog.shared.extensions

inline fun <T> lazyFast(crossinline operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}

@Suppress("NOTHING_TO_INLINE")
inline fun throwNotHandled(message: String = ""): Nothing {
    throw IllegalStateException("state not handled $message")
}
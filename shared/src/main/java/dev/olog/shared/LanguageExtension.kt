package dev.olog.shared

inline fun <T> lazyFast(crossinline operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}

@Suppress("NOTHING_TO_INLINE")
inline fun throwNotHandled(message: String): Nothing {
    throw IllegalStateException("state not handled $message")
}

/**
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject) {
 *     is OneType -> //
 *     is AnotherType -> //
 * }.exhaustive
 */
inline val <T> T.exhaustive: T
    get() = this

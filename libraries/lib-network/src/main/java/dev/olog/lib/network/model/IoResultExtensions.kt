package dev.olog.lib.network.model

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> IoResult<T>.getOrNull(): T? = getOrDefault { null }

inline fun <T> IoResult<T>.getOrDefault(crossinline orDefault: () -> T): T {
    if (this is IoResult.Success) {
        return this.data
    }
    return orDefault()
}
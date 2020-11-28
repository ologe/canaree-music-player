@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared

import kotlinx.coroutines.flow.*

inline fun <T, R> Flow<List<T>>.mapListItem(crossinline mapper: (T) -> R): Flow<List<R>> {
    return this.map { it.map(mapper) }
}

inline val <T> MutableSharedFlow<T>.value: T
    get() = replayCache.last()

fun <T, R> Flow<T>.mapWithLatest(
    initialValue: T?,
    mapper: suspend (T?, T) -> R
): Flow<R> {
    var last = initialValue
    return this
        .onEach { last = it }
        .mapLatest { new -> mapper(last, new) }
}
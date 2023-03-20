package dev.olog.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> Flow<List<T>>.mapListItem(crossinline mapper: (T) -> R): Flow<List<R>> {
    return this.map { it.map(mapper) }
}

inline fun <T> Flow<List<T>>.filterListItem(crossinline predicate: (T) -> Boolean): Flow<List<T>> {
    return this.map { it.filter(predicate) }
}
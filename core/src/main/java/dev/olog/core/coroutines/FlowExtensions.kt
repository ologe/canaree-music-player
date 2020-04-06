@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.core.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> Flow<List<T>>.mapListItem(crossinline mapper: (T) -> R): Flow<List<R>> {
    return this.map { it.map(mapper) }
}

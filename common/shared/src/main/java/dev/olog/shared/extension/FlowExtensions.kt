package dev.olog.shared.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

inline fun <T, R> Flow<List<T>>.mapListItem(crossinline mapper: (T) -> R): Flow<List<R>> {
    return this.map { it.map(mapper) }
}

fun <T : Any> Flow<T>.collectOnLifecycle(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collect: FlowCollector<T>,
) {
    owner.lifecycle.coroutineScope.launch {
        flowWithLifecycle(owner.lifecycle, minActiveState)
            .collect(collect)
    }
}
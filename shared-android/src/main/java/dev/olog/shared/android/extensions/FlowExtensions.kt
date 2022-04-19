package dev.olog.shared.android.extensions

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

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

fun <T : Any> Flow<T>.awaitLifecycle(
    owner: LifecycleOwner,
    level: Lifecycle.State,
): Flow<T> = transform { value ->
    owner.lifecycle.whenStateAtLeast(level) {
        emit(value)
    }
}
package dev.olog.shared.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import dev.olog.shared.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asFlowable

fun <T : Any> Flow<T>.asLiveData(): LiveData<T> {
    // TODO using this way because it handles backpressuepr correctly, check
    // in the future in if provided and official way
    return LiveDataReactiveStreams.fromPublisher(this.asFlowable())
}

inline fun <T, R> Flow<List<T>>.mapListItem(crossinline mapper: (T) -> R): Flow<List<R>> {
    return this.map { it.map { mapper(it) } }
}

fun <T> Flow<T>.assertBackground(): Flow<T> {
    return flow {
        assertBackgroundThread()
        collect { emit(it) }
    }
}
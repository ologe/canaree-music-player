package dev.olog.shared.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import dev.olog.shared.utils.assertBackgroundThread
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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


fun <T, R, S, U> Flow<T>.combineLatest(
    flow2: Flow<R>,
    flow3: Flow<S>,
    combiner: (T, R, S) -> U
): Flow<U> {
    return flow {
        coroutineScope {
            var result1: T? = null
            var result2: R? = null
            var result3: S? = null
            launch {
                collect {
                    result1 = it
                    if (result1 != null && result2 != null && result3 != null) {
                        emit(combiner(result1!!, result2!!, result3!!))
                    }
                }
            }
            launch {
                flow2.collect {
                    result2 = it
                    if (result1 != null && result2 != null && result3 != null) {
                        emit(combiner(result1!!, result2!!, result3!!))
                    }
                }
            }
            launch {
                flow3.collect {
                    result3 = it
                    if (result1 != null && result2 != null && result3 != null) {
                        emit(combiner(result1!!, result2!!, result3!!))
                    }
                }
            }
        }
    }
}

fun <T, R, S, U, V> Flow<T>.combineLatest(
    flow2: Flow<R>,
    flow3: Flow<S>,
    flow4: Flow<U>,
    combiner: (T, R, S, U) -> V
): Flow<V> {
    return flow {
        coroutineScope {
            var result1: T? = null
            var result2: R? = null
            var result3: S? = null
            var result4: U? = null
            launch {
                collect {
                    result1 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null) {
                        emit(combiner(result1!!, result2!!, result3!!, result4!!))
                    }
                }
            }
            launch {
                flow2.collect {
                    result2 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null) {
                        emit(combiner(result1!!, result2!!, result3!!, result4!!))
                    }
                }
            }
            launch {
                flow3.collect {
                    result3 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null) {
                        emit(combiner(result1!!, result2!!, result3!!, result4!!))
                    }
                }
            }
            launch {
                flow4.collect {
                    result4 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null) {
                        emit(combiner(result1!!, result2!!, result3!!, result4!!))
                    }
                }
            }
        }
    }
}


fun <T, R, S, U, V, W, X> Flow<T>.combineLatest(
    flow2: Flow<R>,
    flow3: Flow<S>,
    flow4: Flow<U>,
    flow5: Flow<V>,
    flow6: Flow<W>,
    combiner: (T, R, S, U, V, W) -> X
): Flow<X> {

    return flow {
        coroutineScope {
            var result1: T? = null
            var result2: R? = null
            var result3: S? = null
            var result4: U? = null
            var result5: V? = null
            var result6: W? = null
            launch {
                collect {
                    result1 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null && result5 != null && result6 != null) {
                        emit(
                            combiner(
                                result1!!,
                                result2!!,
                                result3!!,
                                result4!!,
                                result5!!,
                                result6!!
                            )
                        )
                    }
                }
            }
            launch {
                flow2.collect {
                    result2 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null && result5 != null && result6 != null) {
                        emit(
                            combiner(
                                result1!!,
                                result2!!,
                                result3!!,
                                result4!!,
                                result5!!,
                                result6!!
                            )
                        )
                    }
                }
            }
            launch {
                flow3.collect {
                    result3 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null && result5 != null && result6 != null) {
                        emit(
                            combiner(
                                result1!!,
                                result2!!,
                                result3!!,
                                result4!!,
                                result5!!,
                                result6!!
                            )
                        )
                    }
                }
            }
            launch {
                flow4.collect {
                    result4 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null && result5 != null && result6 != null) {
                        emit(
                            combiner(
                                result1!!,
                                result2!!,
                                result3!!,
                                result4!!,
                                result5!!,
                                result6!!
                            )
                        )
                    }
                }
            }
            launch {
                flow5.collect {
                    result5 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null && result5 != null && result6 != null) {
                        emit(
                            combiner(
                                result1!!,
                                result2!!,
                                result3!!,
                                result4!!,
                                result5!!,
                                result6!!
                            )
                        )
                    }
                }
            }
            launch {
                flow6.collect {
                    result6 = it
                    if (result1 != null && result2 != null && result3 != null && result4 != null && result5 != null && result6 != null) {
                        emit(
                            combiner(
                                result1!!,
                                result2!!,
                                result3!!,
                                result4!!,
                                result5!!,
                                result6!!
                            )
                        )
                    }
                }
            }
        }
    }
}
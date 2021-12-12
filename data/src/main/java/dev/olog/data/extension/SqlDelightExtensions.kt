package dev.olog.data.extension

import com.squareup.sqldelight.Query
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

private fun <T : Any> Query<T>.asFlow(): Flow<Query<T>> = flow {
    val channel = Channel<Unit>(Channel.CONFLATED)
    channel.trySend(Unit)

    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            channel.trySend(Unit)
        }
    }

    addListener(listener)
    try {
        for (item in channel) {
            emit(this@asFlow)
        }
    } finally {
        removeListener(listener)
    }
}

fun <T : Any> Query<T>.mapToFlowOne(context: CoroutineContext): Flow<T> {
    return asFlow()
        .map { it.executeAsOne() }
        .flowOn(context)
}

fun <T : Any> Query<T>.mapToFlowOneOrDefault(
    defaultValue: T,
    context: CoroutineContext
): Flow<T> {
    return asFlow()
        .map { it.executeAsOneOrNull() ?: defaultValue }
        .flowOn(context)
}

fun <T : Any> Query<T>.mapToFlowOneOrNull(context: CoroutineContext): Flow<T?> {
    return asFlow()
        .map { it.executeAsOneOrNull() }
        .flowOn(context)
}

fun <T : Any> Query<T>.mapToFlowOneNotNull(context: CoroutineContext): Flow<T> {
    return asFlow()
        .mapNotNull { it.executeAsOneOrNull() }
        .flowOn(context)
}

fun <T : Any> Query<T>.mapToFlowList(context: CoroutineContext): Flow<List<T>> {
    return asFlow()
        .map { it.executeAsList() }
        .flowOn(context)
}

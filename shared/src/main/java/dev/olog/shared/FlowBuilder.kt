package dev.olog.shared

import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

@Suppress("FunctionName")
fun FlowInterval(duration: Duration): Flow<Int> {
    return flow {
        var tick = 0
        emit(tick)
        while (true){
            delay(duration.toLongMilliseconds())
            emit(++tick)
        }
    }
}

@Suppress("FunctionName")
fun<T> ConflatedSharedFlow(initialValue: T): MutableSharedFlow<T> {
    val flow =  MutableSharedFlow<T>(replay = 1, onBufferOverflow = DROP_OLDEST)
    flow.tryEmit(initialValue)
    return flow
}
package dev.olog.shared

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun flowInterval(duration: Duration): Flow<Int> {
    return flow {
        var tick = 0
        emit(tick)
        while (true){
            delay(duration.toLongMilliseconds())
            emit(++tick)
        }
    }
}
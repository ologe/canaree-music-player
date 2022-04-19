package dev.olog.shared

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

fun flowInterval(interval: Long, timeUnit: TimeUnit): Flow<Int> {
    val delayMillis = timeUnit.toMillis(interval)
    return flow {
        var tick = 0
        emit(tick)
        while (currentCoroutineContext().isActive) {
            delay(delayMillis)
            emit(++tick)
        }
    }
}
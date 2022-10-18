package dev.olog.shared.android.extensions

import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.assertBackground(): Flow<T> {
    return channelFlow {
        assertBackgroundThread()
        collect { trySend(it) }
    }
}
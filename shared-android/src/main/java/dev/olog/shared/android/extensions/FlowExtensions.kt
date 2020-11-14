package dev.olog.shared.android.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn

// TODO delete
@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.assertBackground(): Flow<T> {
    return channelFlow {
        assertBackgroundThread()
        collect { offer(it) }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.launchIn(fragment: Fragment): Job {
    return launchIn(fragment.viewLifecycleOwner.lifecycleScope)
}
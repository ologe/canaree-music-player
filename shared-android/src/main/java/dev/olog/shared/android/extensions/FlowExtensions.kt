package dev.olog.shared.android.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.launchIn(fragment: Fragment): Job {
    return launchIn(fragment.viewLifecycleOwner.lifecycleScope)
}
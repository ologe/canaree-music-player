package dev.olog.msc.theme

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.olog.core.Preference
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseThemeUpdater<T>(
    private val scope: CoroutineScope,
    private val preference: Preference<T>
) : DefaultLifecycleObserver {

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private var job by autoDisposeJob()

    final override fun onStart(owner: LifecycleOwner) {
        job = preference.observe()
            .drop(1) // skip initial value
            .onEach { onPrefsChanged(it) }
            .launchIn(scope)
    }

    final override fun onStop(owner: LifecycleOwner) {
        job = null
    }

    protected abstract fun onPrefsChanged(value: T)


}
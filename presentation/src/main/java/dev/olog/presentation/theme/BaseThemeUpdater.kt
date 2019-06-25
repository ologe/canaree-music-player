package dev.olog.presentation.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.olog.core.dagger.ApplicationContext

abstract class BaseThemeUpdater(
    @ApplicationContext protected val context: Context,
    protected val prefs: SharedPreferences,
    protected val key: String

) : DefaultLifecycleObserver,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var hasInit: Boolean = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        tryInitialize()
    }

    override fun onStart(owner: LifecycleOwner) {
        tryInitialize()
    }

    override fun onStop(owner: LifecycleOwner) {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun tryInitialize() {
        if (!hasInit) {
            prefs.registerOnSharedPreferenceChangeListener(this)
            onPrefsChanged(true)
            hasInit = true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            this.key -> onPrefsChanged(false)
        }
    }

    protected abstract fun onPrefsChanged(forced: Boolean)


}
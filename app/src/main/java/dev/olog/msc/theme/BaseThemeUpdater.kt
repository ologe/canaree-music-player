package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

abstract class BaseThemeUpdater<T>(
    protected val context: Context,
    protected val prefs: SharedPreferences,
    protected val key: String

) : DefaultLifecycleObserver,
    SharedPreferences.OnSharedPreferenceChangeListener {


    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            this.key -> onPrefsChanged()
        }
    }

    protected abstract fun onPrefsChanged()

    protected abstract fun getValue(): T


}
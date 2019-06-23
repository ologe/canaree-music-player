package dev.olog.presentation.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.R
import dev.olog.shared.isQ
import javax.inject.Inject

class DarkMode @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) : DefaultLifecycleObserver,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var currentActivity: Activity? = null

    private var hasInit: Boolean = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        tryInitialize()
    }

    override fun onStart(owner: LifecycleOwner) {
        tryInitialize()
    }

    private fun tryInitialize() {
        if (!hasInit) {
            prefs.registerOnSharedPreferenceChangeListener(this)
            setInitialValue()
            hasInit = true
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            context.getString(R.string.prefs_dark_mode_key) -> onThemeChanged(
                prefs.getString(key, context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system))!!, true
            )
        }
    }

    private fun setInitialValue() {
        val initialTheme = prefs.getString(
            context.getString(R.string.prefs_dark_mode_key),
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system)
        )!!
        onThemeChanged(initialTheme, false)
    }

    private fun onThemeChanged(theme: String, recreate: Boolean) {
        val darkMode = when (theme) {
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system) -> {
                if (isQ()) {
                    // TODO update when Q will be releases
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            context.getString(R.string.prefs_dark_mode_2_entry_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.prefs_dark_mode_2_entry_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw IllegalStateException("invalid theme=$theme")
        }
        AppCompatDelegate.setDefaultNightMode(darkMode)
        if (recreate){
            currentActivity?.recreate()
        }
    }

    fun updateCurrentActivity(activity: Activity){
        currentActivity = activity
    }

}


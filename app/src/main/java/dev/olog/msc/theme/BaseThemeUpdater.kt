package dev.olog.msc.theme

import android.content.SharedPreferences

abstract class BaseThemeUpdater(
    protected val key: String
) : SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        when (key) {
            this.key -> onPrefsChanged()
        }
    }

    protected abstract fun onPrefsChanged()


}
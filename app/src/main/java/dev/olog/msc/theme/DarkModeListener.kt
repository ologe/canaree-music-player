package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.msc.R
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.shared.android.utils.isQ
import javax.inject.Inject

// TODO check if dark is still needed to call recreate
internal class DarkModeListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) : BaseThemeUpdater(
    key = context.getString(R.string.prefs_dark_mode_key)
), ActivityLifecycleCallbacks by CurrentActivityObserver(context) {

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
        AppCompatDelegate.setDefaultNightMode(fetchValue())
    }

    override fun onPrefsChanged() {
        AppCompatDelegate.setDefaultNightMode(fetchValue())
    }

    private fun fetchValue(): Int {
        val value = prefs.getString(key, context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system))

        return when (value) {
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system) -> {
                if (isQ()) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            context.getString(R.string.prefs_dark_mode_2_entry_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.prefs_dark_mode_2_entry_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> error("invalid theme=$value")
        }
    }
}


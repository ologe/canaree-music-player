package dev.olog.msc.theme

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import dev.olog.core.dagger.ApplicationContext
import dev.olog.msc.ActivityLifecycleCallbacks
import dev.olog.presentation.R
import dev.olog.shared.utils.isQ
import javax.inject.Inject

internal class DarkModeListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater<Boolean>(context, prefs, context.getString(R.string.prefs_dark_mode_key)),
    ActivityLifecycleCallbacks by CurrentActivityObserver(context) {

    override fun onPrefsChanged() {
        val value = prefs.getString(
            key,
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system)
        )

        val darkMode = when (value) {
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system) -> {
                if (isQ()) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY // TODO update what the string value when pre Q in prefs
                }
            }
            context.getString(R.string.prefs_dark_mode_2_entry_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.prefs_dark_mode_2_entry_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw IllegalStateException("invalid theme=$value")
        }
        AppCompatDelegate.setDefaultNightMode(darkMode)
        currentActivity?.recreate()
    }

    override fun getValue(): Boolean {
        TODO("not implemented")
    }
}

internal class CurrentActivityObserver(context: Context) : ActivityLifecycleCallbacks {

    override var currentActivity: Activity? = null
        private set

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

}


package dev.olog.shared.android.dark.mode

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dev.olog.core.isQ
import dev.olog.shared.android.R

object DarkModeUtils {

    @JvmStatic
    fun fromString(context: Context, value: String): Int {
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
            else -> throw IllegalStateException("invalid theme=$value")
        }
    }

}

@Suppress("NOTHING_TO_INLINE")
inline fun Context.isDarkMode(): Boolean {
    return resources.getBoolean(R.bool.is_dark_mode)
}
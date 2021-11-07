package dev.olog.shared.android.theme

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import dev.olog.shared.android.utils.isQ

enum class DarkMode(@StringRes val prefValue: Int, val appCompatValue: Int) {
    FollowSystem(
        prefs.R.string.prefs_dark_mode_2_entry_value_follow_system,
        if (isQ()) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    ),
    Light(
        prefs.R.string.prefs_dark_mode_2_entry_value_light,
        AppCompatDelegate.MODE_NIGHT_NO
    ),
    Dark(
        prefs.R.string.prefs_dark_mode_2_entry_value_dark,
        AppCompatDelegate.MODE_NIGHT_YES
    );

    companion object {
        fun fromPref(
            context: Context,
            value: String
        ): DarkMode {
            return values().find { context.getString(it.prefValue) == value } ?: FollowSystem
        }
    }

}
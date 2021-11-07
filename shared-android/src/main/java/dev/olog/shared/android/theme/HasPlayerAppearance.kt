package dev.olog.shared.android.theme

import android.content.Context
import androidx.annotation.StringRes
import dev.olog.shared.android.extensions.findInContext

interface HasPlayerAppearance {
    fun playerAppearance(): PlayerAppearance

    fun isDefault() = playerAppearance() == PlayerAppearance.DEFAULT
    fun isFlat() = playerAppearance() == PlayerAppearance.FLAT
    fun isSpotify() = playerAppearance() == PlayerAppearance.SPOTIFY
    fun isFullscreen() = playerAppearance() == PlayerAppearance.FULLSCREEN
    fun isBigImage() = playerAppearance() == PlayerAppearance.BIG_IMAGE
    fun isClean() = playerAppearance() == PlayerAppearance.CLEAN
    fun isMini() = playerAppearance() == PlayerAppearance.MINI
}

enum class PlayerAppearance(@StringRes val prefValue: Int) {
    DEFAULT(prefs.R.string.prefs_appearance_entry_value_default),
    FLAT(prefs.R.string.prefs_appearance_entry_value_flat),
    SPOTIFY(prefs.R.string.prefs_appearance_entry_value_spotify),
    FULLSCREEN(prefs.R.string.prefs_appearance_entry_value_fullscreen),
    BIG_IMAGE(prefs.R.string.prefs_appearance_entry_value_big_image),
    CLEAN(prefs.R.string.prefs_appearance_entry_value_clean),
    MINI(prefs.R.string.prefs_appearance_entry_value_mini);

    companion object {
        fun fromPref(
            context: Context,
            value: String
        ): PlayerAppearance {
            return values().find { context.getString(it.prefValue) == value } ?: DEFAULT
        }
    }
}

fun Context.hasPlayerAppearance(): HasPlayerAppearance = applicationContext.findInContext()
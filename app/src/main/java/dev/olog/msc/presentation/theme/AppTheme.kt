package dev.olog.msc.presentation.theme

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import dev.olog.msc.R
import dev.olog.shared.isP

object AppTheme {

    enum class Theme {
        DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE, CLEAN, MINI;
    }

    enum class Immersive {
        DISABLED, ENABLED
    }

    private var THEME = Theme.DEFAULT
    private var IMMERSIVE_MDOE = Immersive.DISABLED

    fun initialize(app: Application){
        updateTheme(app)
        updateDarkMode(app)
        updateImmersive(app)
    }

    fun isImmersiveMode(): Boolean = IMMERSIVE_MDOE == Immersive.ENABLED

    fun isDefaultTheme(): Boolean = THEME == Theme.DEFAULT
    fun isFlatTheme(): Boolean = THEME == Theme.FLAT
    fun isSpotifyTheme(): Boolean = THEME == Theme.SPOTIFY
    fun isFullscreenTheme(): Boolean = THEME == Theme.FULLSCREEN
    fun isBigImageTheme(): Boolean = THEME == Theme.BIG_IMAGE
    fun isCleanTheme(): Boolean = THEME == Theme.CLEAN
    fun isMiniTheme(): Boolean = THEME == Theme.MINI

    fun updateTheme(context: Context){
        THEME = getTheme(context)
    }

    fun updateDarkMode(context: Context){
        val darkMode = getDarkMode(context)
        AppCompatDelegate.setDefaultNightMode(darkMode)
        // TODO needs to recraete ??
    }

    fun updateImmersive(context: Context){
        IMMERSIVE_MDOE = getImmersiveMode(context)
    }

    private fun getImmersiveMode(context: Context): Immersive {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val isImmersive = prefs.getBoolean(context.getString(R.string.prefs_immersive_key), false)
        return when {
            isImmersive -> Immersive.ENABLED
            else -> Immersive.DISABLED
        }
    }

    private fun getTheme(context: Context): Theme {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val theme = prefs.getString(context.getString(R.string.prefs_appearance_key), context.getString(R.string.prefs_appearance_entry_value_default))
        return when (theme) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> Theme.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> Theme.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> Theme.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> Theme.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> Theme.BIG_IMAGE
            context.getString(R.string.prefs_appearance_entry_value_clean) -> Theme.CLEAN
            context.getString(R.string.prefs_appearance_entry_value_mini) -> Theme.MINI
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    private fun getDarkMode(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val theme = prefs.getString(
            context.getString(R.string.prefs_dark_mode_key),
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system))
        return when (theme) {
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system) -> {
                if (isP()) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            context.getString(R.string.prefs_dark_mode_2_entry_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.prefs_dark_mode_2_entry_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

}
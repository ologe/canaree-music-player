package dev.olog.msc.presentation.theme

import android.app.Application
import android.content.Context
import android.support.v7.preference.PreferenceManager
import dev.olog.msc.R

object AppTheme {

    enum class Theme {
        DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE, CLEAN;
    }

    enum class DarkMode {
        NONE, LIGHT, DARK, BLACK
    }

    private var THEME = Theme.DEFAULT
    private var DARK_MODE = DarkMode.NONE

    fun initialize(app: Application){
        updateTheme(app)
        updateDarkMode(app)
    }

    fun isDefault(): Boolean = THEME == Theme.DEFAULT
    fun isFlat(): Boolean = THEME == Theme.FLAT
    fun isSpotify(): Boolean = THEME == Theme.SPOTIFY
    fun isFullscreen(): Boolean = THEME == Theme.FULLSCREEN
    fun isBigImage(): Boolean = THEME == Theme.BIG_IMAGE
    fun isClean(): Boolean = THEME == Theme.CLEAN

    fun isWhiteMode(): Boolean = DARK_MODE == DarkMode.NONE
    fun isGrayMode(): Boolean = DARK_MODE == DarkMode.LIGHT
    fun isDarkMode(): Boolean = DARK_MODE == DarkMode.DARK
    fun isBlackMode(): Boolean = DARK_MODE == DarkMode.BLACK

    fun isWhiteTheme(): Boolean = DARK_MODE == DarkMode.NONE || DARK_MODE == DarkMode.LIGHT
    fun isDarkTheme(): Boolean = DARK_MODE == DarkMode.DARK || DARK_MODE == DarkMode.BLACK

    fun updateTheme(context: Context){
        THEME = getTheme(context)
    }

    fun updateDarkMode(context: Context){
        DARK_MODE = getDarkMode(context)
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
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    private fun getDarkMode(context: Context): DarkMode {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val theme = prefs.getString(context.getString(R.string.prefs_dark_mode_key), context.getString(R.string.prefs_dark_mode_entry_value_white))
        return when (theme) {
            context.getString(R.string.prefs_dark_mode_entry_value_white) -> DarkMode.NONE
            context.getString(R.string.prefs_dark_mode_entry_value_gray) -> DarkMode.LIGHT
            context.getString(R.string.prefs_dark_mode_entry_value_dark) -> DarkMode.DARK
            context.getString(R.string.prefs_dark_mode_entry_value_black) -> DarkMode.BLACK
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

}
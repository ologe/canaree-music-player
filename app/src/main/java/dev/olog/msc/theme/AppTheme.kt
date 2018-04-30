package dev.olog.msc.theme

import android.content.Context
import android.support.v7.preference.PreferenceManager
import dev.olog.msc.R

object AppTheme {

    enum class Theme {
        DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE;
    }

    enum class DarkMode {
        NONE, LIGHT, DARK, BLACK
    }

    private var THEME = Theme.DEFAULT

    fun initialize(context: Context){
        updateTheme(context)
    }

    fun isDefault(): Boolean = THEME == Theme.DEFAULT
    fun isFlat(): Boolean = THEME == Theme.FLAT
    fun isSpotify(): Boolean = THEME == Theme.SPOTIFY
    fun isFullscreen(): Boolean = THEME == Theme.FULLSCREEN
    fun isBigImage(): Boolean = THEME == Theme.BIG_IMAGE

    fun updateTheme(context: Context){
        THEME = getTheme(context)
    }

    private fun getTheme(context: Context): Theme {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val theme = prefs.getString(context.getString(R.string.prefs_appearance_key), context.getString(R.string.prefs_appearance_entry_value_default))
        return when (theme) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> Theme.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> Theme.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> Theme.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> Theme.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> Theme.BIG_IMAGE
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

}
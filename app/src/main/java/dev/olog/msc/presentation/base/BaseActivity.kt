package dev.olog.msc.presentation.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import dev.olog.msc.R
import dev.olog.msc.dagger.base.DaggerActivity
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.setLightStatusBar

abstract class BaseActivity : DaggerActivity(), ThemedActivity {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        disableDayNight()
        setTheme(getActivityTheme())
        themeAccentColor(theme)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    private fun disableDayNight(){
        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @StyleRes
    private fun getActivityTheme() = when {
        AppTheme.isWhiteMode() -> R.style.AppThemeWhite
        AppTheme.isGrayMode() -> R.style.AppThemeGray
        AppTheme.isDarkMode() -> R.style.AppThemeDark
        AppTheme.isBlackMode() -> R.style.AppThemeBlack
        else -> throw IllegalStateException("invalid theme")
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : androidx.fragment.app.Fragment> findFragmentByTag(tag: String): T? {
        return supportFragmentManager.findFragmentByTag(tag) as T?
    }


}

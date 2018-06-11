package dev.olog.msc.presentation.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.msc.Permissions
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.setLightStatusBar

abstract class BaseActivity : DaggerAppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        disableDayNight()
        setTheme(getActivityTheme())
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
        Permissions.requestForegroundService(this)
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
    internal fun <T : Fragment> findFragmentByTag(tag: String): T? {
        return supportFragmentManager.findFragmentByTag(tag) as T?
    }

}

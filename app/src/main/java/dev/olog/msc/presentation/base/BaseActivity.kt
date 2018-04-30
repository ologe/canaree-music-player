package dev.olog.msc.presentation.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.theme.AppTheme
import dev.olog.msc.utils.k.extension.setLightStatusBar

abstract class BaseActivity : DaggerAppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getActivityTheme())
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
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

package dev.olog.msc.presentation.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.setLightStatusBar

abstract class BaseActivity : DaggerAppCompatActivity(), ThemedActivity {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getActivityTheme())
        themeAccentColor(this, theme)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && AppTheme.isImmersiveMode()){
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }


}

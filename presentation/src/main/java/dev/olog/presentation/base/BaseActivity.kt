package dev.olog.presentation.base

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatDelegate
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.lib.DarkDesaturatedResources
import dev.olog.presentation.R
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.android.theme.ThemedActivity
import dev.olog.shared.android.theme.themeManager
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), ThemedActivity, HasAndroidInjector {

    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private var customResources: Resources? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        setDarkMode()
        themeAccentColor(this, theme)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && themeManager.isImmersive) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun getResources(): Resources {
        if (customResources == null){
            val res = super.getResources()
            val isDarkMode = res.getBoolean(R.bool.is_dark_mode)
            customResources = DarkDesaturatedResources(isDarkMode, res)
        }
        return customResources!!
    }

    private fun setDarkMode() {
        val darkMode = themeManager.darkMode
        AppCompatDelegate.setDefaultNightMode(darkMode)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

}

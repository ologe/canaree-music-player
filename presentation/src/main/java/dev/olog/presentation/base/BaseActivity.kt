package dev.olog.presentation.base

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import dev.olog.lib.DarkDesaturatedResources
import dev.olog.presentation.R
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.android.theme.immersiveAmbient

abstract class BaseActivity : AppCompatActivity(), ThemedActivity {

    private var customResources: Resources? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        themeAccentColor(this, theme)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && immersiveAmbient.isEnabled) {
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

}

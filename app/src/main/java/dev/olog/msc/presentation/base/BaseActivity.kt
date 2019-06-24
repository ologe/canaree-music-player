package dev.olog.msc.presentation.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.setLightStatusBar
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), ThemedActivity, HasSupportFragmentInjector {

    @Inject
    internal lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        injectComponent()
        themeAccentColor(this, theme)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Fragment> findFragmentByTag(tag: String): T? {
        return supportFragmentManager.findFragmentByTag(tag) as T?
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && AppTheme.isImmersiveMode()) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    protected open fun injectComponent() {}


    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

}

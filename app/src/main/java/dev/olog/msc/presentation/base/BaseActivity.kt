package dev.olog.msc.presentation.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.msc.utils.k.extension.setLightStatusBar

abstract class BaseActivity : DaggerAppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Fragment> findFragmentByTag(tag: String): T? {
        return supportFragmentManager.findFragmentByTag(tag) as T?
    }

}

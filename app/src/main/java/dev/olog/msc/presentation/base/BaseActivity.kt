package dev.olog.msc.presentation.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.msc.utils.k.extension.setLightStatusBar
import dev.olog.shared_android.isMarshmallow

abstract class BaseActivity : DaggerAppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isMarshmallow()) {
            window.setLightStatusBar()
        }

        intent?.let { handleIntent(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Fragment> findFragmentByTag(tag: String): T? {
        return supportFragmentManager.findFragmentByTag(tag) as T?
    }

    protected open fun handleIntent(intent: Intent){
    }

}

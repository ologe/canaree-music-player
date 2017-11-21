package dev.olog.presentation._base

import android.os.Bundle
import android.support.annotation.CallSuper
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.presentation.utils.setTransparentStatusBar

abstract class BaseActivity : DaggerAppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setLightStatusBar()
        window.setTransparentStatusBar()

    }

}

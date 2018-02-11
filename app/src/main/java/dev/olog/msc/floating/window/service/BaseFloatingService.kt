package dev.olog.msc.floating.window.service

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ServiceLifecycleDispatcher
import android.support.annotation.CallSuper
import dagger.android.AndroidInjection
import dev.olog.msc.floating.window.service.api.window.HoverMenuService

abstract class BaseFloatingService : HoverMenuService(), LifecycleOwner{

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @CallSuper
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    @CallSuper
    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle

}
package dev.olog.service.floating

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import dev.olog.service.floating.api.window.HoverMenuService

abstract class BaseFloatingService : HoverMenuService(), LifecycleOwner {

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @CallSuper
    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle

}
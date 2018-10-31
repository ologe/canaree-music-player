package dev.olog.msc.floating.window.service

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import dagger.android.AndroidInjection
import dev.olog.msc.analytics.AppAnalytics
import dev.olog.msc.floating.window.service.api.window.HoverMenuService
import dev.olog.msc.presentation.base.LoggableService

abstract class BaseFloatingService : HoverMenuService(), LifecycleOwner, LoggableService {

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @CallSuper
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        AppAnalytics.trackServiceStart(this)
    }

    @CallSuper
    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
        AppAnalytics.trackServiceEnd(this)
    }

    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle

}
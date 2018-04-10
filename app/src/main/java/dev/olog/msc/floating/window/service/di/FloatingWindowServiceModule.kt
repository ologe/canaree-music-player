package dev.olog.msc.floating.window.service.di

import android.app.NotificationManager
import android.app.Service
import android.arch.lifecycle.Lifecycle
import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ServiceContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.floating.window.service.FloatingWindowService

@Module
class FloatingWindowServiceModule(
        private val service: FloatingWindowService
) {

    @Provides
    @ServiceLifecycle
    fun provideLifecycle(): Lifecycle = service.lifecycle

    @Provides
    @ServiceContext
    fun provideContext(): Context = service

    @Provides
    fun provideService() : Service = service

    @Provides
    internal fun provideNotificationManager(): NotificationManager {
        return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}
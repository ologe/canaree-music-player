package dev.olog.floating_info.di

import android.app.NotificationManager
import android.app.Service
import android.arch.lifecycle.Lifecycle
import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.floating_info.FloatingInfoService
import dev.olog.shared_android.extension.notificationManager

@Module
class FloatingInfoServiceModule(
        private val service: FloatingInfoService
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
        return service.notificationManager
    }

}
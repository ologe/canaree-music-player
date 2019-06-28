package dev.olog.msc.floating.window.service.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.injection.dagger.ServiceContext
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.msc.floating.window.service.FloatingWindowService

@Module
abstract class FloatingWindowServiceModule {

    @Binds
    @ServiceContext
    abstract fun provideContext(instance: FloatingWindowService): Context

    @Binds
    abstract fun provideService(instance: FloatingWindowService): Service

    @Module
    companion object {

        @Provides
        @JvmStatic
        @ServiceLifecycle
        fun provideLifecycle(instance: FloatingWindowService): Lifecycle = instance.lifecycle

        @Provides
        @JvmStatic
        internal fun provideNotificationManager(instance: FloatingWindowService): NotificationManager {
            return instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }


}
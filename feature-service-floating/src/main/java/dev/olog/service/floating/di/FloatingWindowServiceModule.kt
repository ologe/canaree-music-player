package dev.olog.service.floating.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.ServiceContext
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.service.floating.FloatingWindowService

@Module
abstract class FloatingWindowServiceModule {

    @Binds
    @ServiceContext
    abstract fun provideContext(instance: FloatingWindowService): Context

    @Binds
    abstract fun provideService(instance: FloatingWindowService): Service

    companion object {

        @Provides
        @ServiceLifecycle
        fun provideLifecycle(instance: FloatingWindowService): Lifecycle = instance.lifecycle

        @Provides
        internal fun provideNotificationManager(instance: FloatingWindowService): NotificationManager {
            return instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }


}
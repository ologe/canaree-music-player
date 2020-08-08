package dev.olog.feature.service.floating.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
object FloatingWindowModule {

    @Provides
    internal fun provideNotificationManager(instance: Service): NotificationManager {
        return instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}


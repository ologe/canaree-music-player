package dev.olog.msc.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    internal fun provideNotificationManager(service: Service): NotificationManager {
        return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    fun provideLifecycleOwner(service: Service): LifecycleOwner = service as LifecycleOwner

}
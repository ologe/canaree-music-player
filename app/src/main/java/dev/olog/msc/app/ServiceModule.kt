package dev.olog.msc.app

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dev.olog.core.ServiceLifecycle

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    @ServiceLifecycle
    fun provideLifecycle(service: Service): Lifecycle {
        require(service is LifecycleOwner)
        return service.lifecycle
    }

    @Provides
    fun provideNotificationManager(instance: Service): NotificationManager {
        return instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}
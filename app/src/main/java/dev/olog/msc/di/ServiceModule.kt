package dev.olog.msc.di

import android.app.Service
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dev.olog.core.dagger.ServiceLifecycle

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    @ServiceLifecycle
    fun provideServiceLifecycle(service: Service): Lifecycle {
        return (service as LifecycleOwner).lifecycle
    }

}
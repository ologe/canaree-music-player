package dev.olog.msc.di

import android.app.Service
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dev.olog.shared.android.ServiceLifecycle
import dev.olog.shared.android.extensions.findInContext

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    @ServiceLifecycle
    fun provideServiceLifecycle(service: Service): Lifecycle = (service.findInContext<LifecycleOwner>()).lifecycle

}
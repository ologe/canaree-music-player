package dev.olog.msc.di

import android.app.Service
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.ServiceScope
import dev.olog.platform.ServiceLifecycle
import dev.olog.shared.extension.findInContext

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    @ServiceLifecycle
    fun provideServiceLifecycle(service: Service): Lifecycle = (service.findInContext<LifecycleOwner>()).lifecycle

    @Provides
    @ServiceScoped
    fun provideServiceScope(@ServiceLifecycle lifecycle: Lifecycle): ServiceScope = ServiceScope(lifecycle)

}
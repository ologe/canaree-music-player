package dev.olog.service.floating.dagger

import android.app.Service
import androidx.lifecycle.LifecycleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
internal object FloatingWindowServiceModule {

    @Provides
    fun provideLifecycleService(service: Service): LifecycleService {
        require(service is LifecycleService)
        return service
    }

}
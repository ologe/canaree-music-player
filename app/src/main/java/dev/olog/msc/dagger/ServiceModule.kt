package dev.olog.msc.dagger

import android.app.Service
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dev.olog.core.dagger.ServiceContext
import dev.olog.core.dagger.ServiceLifecycle

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceModule {

    @Binds
    @ServiceContext
    internal abstract fun provideContext(instance: Service): Context

    companion object {

        @Provides
        @ServiceLifecycle
        fun provideLifecycle(instance: Service): Lifecycle {
            require(instance is LifecycleOwner)
            return instance.lifecycle
        }

    }

}
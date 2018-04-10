package dev.olog.msc.floating.window.service.di

import android.app.Service
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ServiceKey
import dagger.multibindings.IntoMap
import dev.olog.msc.floating.window.service.FloatingWindowService


@Module(subcomponents = arrayOf(FloatingWindowServiceSubComponent::class))
abstract class FloatingWindowServiceInjector {

    @Binds
    @IntoMap
    @ServiceKey(FloatingWindowService::class)
    internal abstract fun injectorFactory(builder: FloatingWindowServiceSubComponent.Builder)
            : AndroidInjector.Factory<out Service>

}
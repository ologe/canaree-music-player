package dev.olog.msc.floating.window.di

import android.app.Service
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ServiceKey
import dagger.multibindings.IntoMap
import dev.olog.msc.floating.window.FloatingInfoService


@Module(subcomponents = arrayOf(FloatingInfoServiceSubComponent::class))
abstract class FloatingInfoServiceInjector {

    @Binds
    @IntoMap
    @ServiceKey(FloatingInfoService::class)
    internal abstract fun injectorFactory(builder: FloatingInfoServiceSubComponent.Builder)
            : AndroidInjector.Factory<out Service>

}
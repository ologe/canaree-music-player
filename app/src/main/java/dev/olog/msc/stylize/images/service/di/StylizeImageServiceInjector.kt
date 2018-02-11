package dev.olog.msc.stylize.images.service.di

import android.app.Service
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ServiceKey
import dagger.multibindings.IntoMap
import dev.olog.msc.stylize.images.service.StylizeImageService


@Module(subcomponents = arrayOf(StylizeImageServiceSubComponent::class))
abstract class StylizeImageServiceInjector {

    @Binds
    @IntoMap
    @ServiceKey(StylizeImageService::class)
    internal abstract fun injectorFactory(builder: StylizeImageServiceSubComponent.Builder)
            : AndroidInjector.Factory<out Service>

}

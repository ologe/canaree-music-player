package dev.olog.msc.stylize.images.service.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerService
import dev.olog.msc.stylize.images.service.StylizeImageService

@Subcomponent(modules = arrayOf(
        StylizeImageServiceModule::class
))
@PerService
interface StylizeImageServiceSubComponent : AndroidInjector<StylizeImageService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<StylizeImageService>() {

        abstract fun module(module: StylizeImageServiceModule): Builder

        override fun seedInstance(instance: StylizeImageService) {
            module(StylizeImageServiceModule(instance))
        }
    }

}
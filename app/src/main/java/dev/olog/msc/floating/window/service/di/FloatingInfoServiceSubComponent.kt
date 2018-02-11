package dev.olog.msc.floating.window.service.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerService
import dev.olog.msc.floating.window.service.FloatingWindowService

@Subcomponent(modules = arrayOf(
        FloatingInfoServiceModule::class
))
@PerService
interface FloatingInfoServiceSubComponent : AndroidInjector<FloatingWindowService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FloatingWindowService>() {

        abstract fun module(module: FloatingInfoServiceModule): Builder

        override fun seedInstance(instance: FloatingWindowService) {
            module(FloatingInfoServiceModule(instance))
        }
    }

}
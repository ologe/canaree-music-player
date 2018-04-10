package dev.olog.msc.floating.window.service.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.floating.window.service.FloatingWindowService

@Subcomponent(modules = arrayOf(
        FloatingWindowServiceModule::class
))
@PerService
interface FloatingWindowServiceSubComponent : AndroidInjector<FloatingWindowService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FloatingWindowService>() {

        abstract fun module(module: FloatingWindowServiceModule): Builder

        override fun seedInstance(instance: FloatingWindowService) {
            module(FloatingWindowServiceModule(instance))
        }
    }

}
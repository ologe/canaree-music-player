package dev.olog.msc.floating.window.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerService
import dev.olog.msc.floating.window.FloatingInfoService
import dev.olog.msc.floating.window.music.service.MusicServiceModule

@Subcomponent(modules = arrayOf(
        FloatingInfoServiceModule::class,
        MusicServiceModule::class
))
@PerService
interface FloatingInfoServiceSubComponent : AndroidInjector<FloatingInfoService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FloatingInfoService>() {

        abstract fun module(module: FloatingInfoServiceModule): Builder

        override fun seedInstance(instance: FloatingInfoService) {
            module(FloatingInfoServiceModule(instance))
        }
    }

}
package dev.olog.floating_info.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.floating_info.FloatingInfoService
import dev.olog.floating_info.music_service.MusicServiceModule

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
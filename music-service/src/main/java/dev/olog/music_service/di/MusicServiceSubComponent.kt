package dev.olog.music_service.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.music_service.MusicService
import dev.olog.music_service.notification.NotificationModule

@Subcomponent(modules = arrayOf(
        MusicServiceModule::class,
        NotificationModule::class
))
@PerService
interface MusicServiceSubComponent : AndroidInjector<MusicService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MusicService>() {

        abstract fun musicServiceModule(module: MusicServiceModule): Builder

        override fun seedInstance(instance: MusicService) {
            musicServiceModule(MusicServiceModule(instance))
        }

    }
}
package dev.olog.msc.music.service.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.music.service.notification.NotificationModule

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
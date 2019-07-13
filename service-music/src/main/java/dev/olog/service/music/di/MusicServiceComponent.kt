package dev.olog.service.music.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.injection.CoreComponent
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.MusicService
import dev.olog.service.music.notification.NotificationModule

internal fun MusicService.inject() {
    val coreComponent = CoreComponent.coreComponent(application)
    DaggerMusicServiceComponent.factory().create(this, coreComponent)
            .inject(this)
}

@Component(modules = [
    MusicServiceModule::class,
    NotificationModule::class
], dependencies = [CoreComponent::class])
@PerService
interface MusicServiceComponent {

    fun inject(instance: MusicService)

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance instance: MusicService, component: CoreComponent): MusicServiceComponent

    }
}
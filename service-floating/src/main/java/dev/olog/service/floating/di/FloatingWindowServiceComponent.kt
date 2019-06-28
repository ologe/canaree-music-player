package dev.olog.service.floating.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.injection.CoreComponent
import dev.olog.injection.dagger.PerService
import dev.olog.service.floating.FloatingWindowService

fun FloatingWindowService.inject(){
    DaggerFloatingWindowServiceComponent.factory()
            .create(this, CoreComponent.coreComponent(application))
            .inject(this)
}

@Component(modules = arrayOf(
        FloatingWindowServiceModule::class
), dependencies = [CoreComponent::class])
@PerService
interface FloatingWindowServiceComponent {

    fun inject(instance: FloatingWindowService)

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance instance: FloatingWindowService, component: CoreComponent): FloatingWindowServiceComponent

    }

}
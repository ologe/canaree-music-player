package dev.olog.msc.floating.window.service.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.CoreComponent
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.floating.window.service.FloatingWindowService

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
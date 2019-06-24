package dev.olog.msc.app

import dagger.Component
import javax.inject.Scope

@Component(dependencies = [CoreComponent::class])
@PerApp
interface AppComponent {

    fun inject(instance: App)

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): AppComponent
    }

}

@Scope
annotation class PerApp
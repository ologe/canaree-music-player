package dev.olog.msc.app

import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.olog.injection.CoreComponent
import dev.olog.msc.appwidgets.WidgetBindingModule
import dev.olog.shared.android.theme.ThemeModule
import javax.inject.Scope

@Component(
    modules = [
        AndroidInjectionModule::class,
        WidgetBindingModule::class,
        ThemeModule::class
    ], dependencies = [CoreComponent::class]
)
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
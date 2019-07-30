package dev.olog.presentation.about.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.olog.injection.CoreComponent
import dev.olog.presentation.ViewModelModule
import dev.olog.presentation.about.AboutActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.model.PresentationModelModule

fun AboutActivity.inject() {
    DaggerAboutActivityComponent.factory()
        .create(this, CoreComponent.coreComponent(application))
        .inject(this)
}

@Component(
    modules = [
        AndroidInjectionModule::class,
        PresentationModelModule::class,
        AboutActivityModule::class,
        ViewModelModule::class
    ], dependencies = [CoreComponent::class]
)
@PerActivity
interface AboutActivityComponent {

    fun inject(instance: AboutActivity)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance instance: AboutActivity,
            component: CoreComponent
        ): AboutActivityComponent
    }

}
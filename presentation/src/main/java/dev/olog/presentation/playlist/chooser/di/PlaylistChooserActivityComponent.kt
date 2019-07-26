package dev.olog.presentation.playlist.chooser.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.olog.injection.CoreComponent
import dev.olog.presentation.ViewModelModule
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.playlist.chooser.PlaylistChooserActivity

fun PlaylistChooserActivity.inject() {
    DaggerPlaylistChooserActivityComponent.factory()
        .create(CoreComponent.coreComponent(application))
        .inject(this)
}

@Component(
    modules = [
        AndroidInjectionModule::class,
        ViewModelModule::class,
        PlaylistChooserActivityModule::class
    ], dependencies = [CoreComponent::class]
)
@PerActivity
interface PlaylistChooserActivityComponent {

    fun inject(instance: PlaylistChooserActivity)

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): PlaylistChooserActivityComponent
    }

}
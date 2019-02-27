package dev.olog.msc.presentation.shortcuts.playlist.chooser.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity


@Module(subcomponents = arrayOf(PlaylistChooserActivitySubComponent::class))
abstract class PlaylistChooserActivityInjector {

    @Binds
    @IntoMap
    @ClassKey(PlaylistChooserActivity::class)
    internal abstract fun injectorFactory(builder: PlaylistChooserActivitySubComponent.Builder)
            : AndroidInjector.Factory<*>

}
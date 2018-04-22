package dev.olog.msc.presentation.shortcuts.playlist.chooser.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity


@Module(subcomponents = arrayOf(PlaylistChooserActivitySubComponent::class))
abstract class PlaylistChooserActivityInjector {

    @Binds
    @IntoMap
    @ActivityKey(PlaylistChooserActivity::class)
    internal abstract fun injectorFactory(builder: PlaylistChooserActivitySubComponent.Builder)
            : AndroidInjector.Factory<out Activity>

}
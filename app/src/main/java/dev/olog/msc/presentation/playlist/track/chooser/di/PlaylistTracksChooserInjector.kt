package dev.olog.msc.presentation.playlist.track.chooser.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment

@Module(subcomponents = arrayOf(PlaylistTracksChooserFragmentSubComponent::class))
abstract class PlaylistTracksChooserInjector {

    @Binds
    @IntoMap
    @ClassKey(PlaylistTracksChooserFragment::class)
    internal abstract fun injectorFactory(builder: PlaylistTracksChooserFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}

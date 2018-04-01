package dev.olog.msc.presentation.playlist.track.chooser.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment

@Module(subcomponents = arrayOf(PlaylistTracksChooserFragmentSubComponent::class))
abstract class PlaylistTracksChooserInjector {

    @Binds
    @IntoMap
    @FragmentKey(PlaylistTracksChooserFragment::class)
    internal abstract fun injectorFactory(builder: PlaylistTracksChooserFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}

package dev.olog.msc.presentation.playlist.track.chooser.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment


@Subcomponent(modules = arrayOf(
        PlaylistTracksChooserFragmentModule::class
))
@PerFragment
interface PlaylistTracksChooserFragmentSubComponent : AndroidInjector<PlaylistTracksChooserFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlaylistTracksChooserFragment>() {

        abstract fun module(module: PlaylistTracksChooserFragmentModule): Builder

        override fun seedInstance(instance: PlaylistTracksChooserFragment) {
            module(PlaylistTracksChooserFragmentModule(instance))
        }
    }

}
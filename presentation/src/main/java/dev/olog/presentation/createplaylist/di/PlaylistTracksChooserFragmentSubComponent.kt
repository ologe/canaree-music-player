package dev.olog.presentation.createplaylist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.createplaylist.PlaylistTracksChooserFragment


@Subcomponent(modules = arrayOf(
        PlaylistTracksChooserFragmentModule::class
))
@PerFragment
interface PlaylistTracksChooserFragmentSubComponent : AndroidInjector<PlaylistTracksChooserFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlaylistTracksChooserFragment>() {

        abstract fun module(module: PlaylistTracksChooserFragmentModule): Builder

        override fun seedInstance(instance: PlaylistTracksChooserFragment) {
            module(
                PlaylistTracksChooserFragmentModule(
                    instance
                )
            )
        }
    }

}
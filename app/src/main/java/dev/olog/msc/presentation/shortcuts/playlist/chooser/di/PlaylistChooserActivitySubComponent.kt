package dev.olog.msc.presentation.shortcuts.playlist.chooser.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerActivity
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity

@Subcomponent(modules = arrayOf(
        PlaylistChooserActivityModule::class
))
@PerActivity
interface PlaylistChooserActivitySubComponent : AndroidInjector<PlaylistChooserActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlaylistChooserActivity>() {

        abstract fun module(module: PlaylistChooserActivityModule): Builder

        override fun seedInstance(instance: PlaylistChooserActivity) {
            module(PlaylistChooserActivityModule(instance))
        }
    }

}
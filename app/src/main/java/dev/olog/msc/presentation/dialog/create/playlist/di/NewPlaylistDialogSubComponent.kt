package dev.olog.msc.presentation.dialog.create.playlist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialog.create.playlist.NewPlaylistDialog


@Subcomponent(modules = arrayOf(
        NewPlaylistDialogModule::class
))
@PerFragment
interface NewPlaylistDialogSubComponent : AndroidInjector<NewPlaylistDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NewPlaylistDialog>() {

        abstract fun module(module: NewPlaylistDialogModule): Builder

        override fun seedInstance(instance: NewPlaylistDialog) {
            module(NewPlaylistDialogModule(instance))
        }
    }

}
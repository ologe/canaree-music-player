package dev.olog.msc.presentation.dialog.clear.playlist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.msc.presentation.dialog.clear.playlist.ClearPlaylistDialog

@Subcomponent(modules = arrayOf(
        ClearPlaylistDialogModule::class
))
@PerFragment
interface ClearPlaylistDialogSubComponent : AndroidInjector<ClearPlaylistDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ClearPlaylistDialog>() {

        abstract fun module(module: ClearPlaylistDialogModule): Builder

        override fun seedInstance(instance: ClearPlaylistDialog) {
            module(ClearPlaylistDialogModule(instance))
        }
    }

}
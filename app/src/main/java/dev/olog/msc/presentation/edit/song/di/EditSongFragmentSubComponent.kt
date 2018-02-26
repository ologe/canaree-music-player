package dev.olog.msc.presentation.edit.song.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.edit.song.EditSongFragment

@Subcomponent(modules = arrayOf(
        EditSongFragmentModule::class
))
@PerFragment
interface EditSongFragmentSubComponent : AndroidInjector<EditSongFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditSongFragment>() {

        abstract fun module(module: EditSongFragmentModule): Builder

        override fun seedInstance(instance: EditSongFragment) {
            module(EditSongFragmentModule(instance))
        }
    }

}
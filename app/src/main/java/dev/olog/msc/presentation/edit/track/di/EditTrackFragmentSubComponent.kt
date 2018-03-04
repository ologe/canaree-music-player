package dev.olog.msc.presentation.edit.track.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.edit.track.EditTrackFragment

@Subcomponent(modules = arrayOf(
        EditTrackFragmentModule::class
))
@PerFragment
interface EditTrackFragmentSubComponent : AndroidInjector<EditTrackFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditTrackFragment>() {

        abstract fun module(module: EditTrackFragmentModule): Builder

        override fun seedInstance(instance: EditTrackFragment) {
            module(EditTrackFragmentModule(instance))
        }
    }

}
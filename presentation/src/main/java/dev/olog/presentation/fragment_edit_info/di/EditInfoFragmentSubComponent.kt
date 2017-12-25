package dev.olog.presentation.fragment_edit_info.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_edit_info.EditInfoFragment

@Subcomponent(modules = arrayOf(
        EditInfoFragmentModule::class
))
@PerFragment
interface EditInfoFragmentSubComponent : AndroidInjector<EditInfoFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditInfoFragment>() {

        abstract fun module(module: EditInfoFragmentModule): Builder

        override fun seedInstance(instance: EditInfoFragment) {
            module(EditInfoFragmentModule(instance))
        }
    }

}
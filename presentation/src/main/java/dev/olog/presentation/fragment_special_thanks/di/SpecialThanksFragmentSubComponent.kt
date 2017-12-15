package dev.olog.presentation.fragment_special_thanks.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_special_thanks.SpecialThanksFragment

@Subcomponent(modules = arrayOf(
        SpecialThanksFragmentModule::class
))
@PerFragment
interface SpecialThanksFragmentSubComponent : AndroidInjector<SpecialThanksFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SpecialThanksFragment>() {

        abstract fun module(module: SpecialThanksFragmentModule): Builder

        override fun seedInstance(instance: SpecialThanksFragment) {
            module(SpecialThanksFragmentModule(instance))
        }
    }

}
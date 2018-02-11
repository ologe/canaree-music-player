package dev.olog.msc.presentation.licenses.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.licenses.LicensesFragment

@Subcomponent(modules = arrayOf(
        LicensesFragmentModule::class
))
@PerFragment
interface LicensesFragmentSubComponent : AndroidInjector<LicensesFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LicensesFragment>() {

        abstract fun module(module: LicensesFragmentModule): Builder

        override fun seedInstance(instance: LicensesFragment) {
            module(LicensesFragmentModule(instance))
        }
    }

}
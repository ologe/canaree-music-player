package dev.olog.presentation.fragment_about.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_about.AboutFragment

@Subcomponent(modules = arrayOf(
        AboutFragmentModule::class
))
@PerFragment
interface AboutFragmentSubComponent : AndroidInjector<AboutFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AboutFragment>() {

        abstract fun module(module: AboutFragmentModule): Builder

        override fun seedInstance(instance: AboutFragment) {
            module(AboutFragmentModule(instance))
        }
    }

}
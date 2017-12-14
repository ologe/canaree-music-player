package dev.olog.presentation.fragment_tab.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_tab.TabFragment

@Subcomponent(modules = arrayOf(
        TabFragmentModule::class,
        TabFragmentViewModelModule::class
))
@PerFragment
interface TabFragmentSubComponent : AndroidInjector<TabFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TabFragment>() {

        abstract fun module(module: TabFragmentModule): Builder

        override fun seedInstance(instance: TabFragment) {
            module(TabFragmentModule(instance))
        }
    }

}
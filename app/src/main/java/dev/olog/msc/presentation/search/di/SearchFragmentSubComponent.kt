package dev.olog.msc.presentation.search.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.presentation.dagger.PerFragment

@Subcomponent(modules = arrayOf(
        SearchFragmentModule::class
))
@PerFragment
interface SearchFragmentSubComponent : AndroidInjector<SearchFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SearchFragment>() {

        abstract fun module(module: SearchFragmentModule): Builder

        override fun seedInstance(instance: SearchFragment) {
            module(SearchFragmentModule(instance))
        }
    }

}
package dev.olog.presentation.fragment_search.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_search.SearchFragment

@Subcomponent(modules = arrayOf(
        SearchFragmentModule::class,
        SearchFragmentViewModelModule::class
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
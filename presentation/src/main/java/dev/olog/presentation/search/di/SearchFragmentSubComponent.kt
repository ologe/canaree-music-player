package dev.olog.presentation.search.di

import dagger.BindsInstance
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.search.SearchFragment

@Subcomponent(modules = [SearchFragmentModule::class])
@PerFragment
interface SearchFragmentSubComponent : AndroidInjector<SearchFragment> {

    @Subcomponent.Factory
    interface Builder : AndroidInjector.Factory<SearchFragment> {

        override fun create(@BindsInstance instance: SearchFragment): AndroidInjector<SearchFragment>
    }

}
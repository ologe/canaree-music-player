package dev.olog.presentation.fragment_detail.recently_added.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerNestedFragment
import dev.olog.presentation.fragment_detail.recently_added.DetailRecentlyAddedFragment

@Subcomponent(modules = arrayOf(
        DetailRecentlyAddedFragmentModule::class
))
@PerNestedFragment
interface DetailRecentlyAddedFragmentSubComponent : AndroidInjector<DetailRecentlyAddedFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DetailRecentlyAddedFragment>() {

        abstract fun module(module: DetailRecentlyAddedFragmentModule): Builder

        override fun seedInstance(instance: DetailRecentlyAddedFragment) {
            module(DetailRecentlyAddedFragmentModule(instance))
        }
    }

}
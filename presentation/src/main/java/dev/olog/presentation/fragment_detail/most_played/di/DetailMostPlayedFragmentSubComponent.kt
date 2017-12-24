package dev.olog.presentation.fragment_detail.most_played.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerNestedFragment
import dev.olog.presentation.fragment_detail.most_played.DetailMostPlayedFragment

@Subcomponent(modules = arrayOf(
        DetailMostPlayedFragmentModule::class
))
@PerNestedFragment
interface DetailMostPlayedFragmentSubComponent : AndroidInjector<DetailMostPlayedFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DetailMostPlayedFragment>() {

        abstract fun module(module: DetailMostPlayedFragmentModule): Builder

        override fun seedInstance(instance: DetailMostPlayedFragment) {
            module(DetailMostPlayedFragmentModule(instance))
        }
    }

}
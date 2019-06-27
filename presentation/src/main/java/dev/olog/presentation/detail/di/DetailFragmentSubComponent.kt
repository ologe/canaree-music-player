package dev.olog.presentation.detail.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.detail.DetailFragment
import dev.olog.presentation.dagger.PerFragment


@Subcomponent(modules = arrayOf(
        DetailFragmentModule::class
))
@PerFragment
internal interface DetailFragmentSubComponent : AndroidInjector<DetailFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DetailFragment>() {

        abstract fun detailModule(module: DetailFragmentModule): Builder

        override fun seedInstance(instance: DetailFragment) {
            detailModule(DetailFragmentModule(instance))
        }
    }

}

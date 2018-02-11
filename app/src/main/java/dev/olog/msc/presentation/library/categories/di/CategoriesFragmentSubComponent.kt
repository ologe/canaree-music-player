package dev.olog.msc.presentation.library.categories.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerFragment
import dev.olog.msc.presentation.library.categories.CategoriesFragment

@Subcomponent(modules = arrayOf(
        CategoriesFragmentModule::class
))
@PerFragment
interface CategoriesFragmentSubComponent : AndroidInjector<CategoriesFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CategoriesFragment>() {

        abstract fun module(module: CategoriesFragmentModule): Builder

        override fun seedInstance(instance: CategoriesFragment) {
            module(CategoriesFragmentModule(instance))
        }
    }

}
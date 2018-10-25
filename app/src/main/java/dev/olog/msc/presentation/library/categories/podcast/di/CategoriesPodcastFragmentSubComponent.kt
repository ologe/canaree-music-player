package dev.olog.msc.presentation.library.categories.podcast.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.library.categories.podcast.CategoriesPodcastFragment

@Subcomponent(modules = arrayOf(
        CategoriesPodcastFragmentModule::class
))
@PerFragment
interface CategoriesPodcastFragmentSubComponent: AndroidInjector<CategoriesPodcastFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CategoriesPodcastFragment>() {

        abstract fun module(module: CategoriesPodcastFragmentModule): Builder

        override fun seedInstance(instance: CategoriesPodcastFragment) {
            module(CategoriesPodcastFragmentModule(instance))
        }
    }

}
package dev.olog.msc.presentation.library.categories.podcast.di

import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ChildFragmentManager
import dev.olog.msc.presentation.library.categories.podcast.CategoriesPodcastFragment

@Module
class CategoriesPodcastFragmentModule(private val fragment: CategoriesPodcastFragment) {

    @Provides
    @ChildFragmentManager
    fun provideFragmentManager() : FragmentManager = fragment.childFragmentManager

}
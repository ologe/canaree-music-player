package dev.olog.msc.presentation.library.categories.track.di

import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ChildFragmentManager
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment

@Module
class CategoriesFragmentModule(private val fragment: CategoriesFragment) {

    @Provides
    @ChildFragmentManager
    fun provideFragmentManager() : androidx.fragment.app.FragmentManager = fragment.childFragmentManager

}
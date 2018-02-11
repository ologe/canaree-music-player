package dev.olog.msc.presentation.library.categories.di

import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.ChildFragmentManager
import dev.olog.msc.presentation.library.categories.CategoriesFragment

@Module
class CategoriesFragmentModule(private val fragment: CategoriesFragment) {

    @Provides
    @ChildFragmentManager
    fun provideFragmentManager() : FragmentManager = fragment.childFragmentManager

}
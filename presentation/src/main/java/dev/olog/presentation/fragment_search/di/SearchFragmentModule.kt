package dev.olog.presentation.fragment_search.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_search.SearchFragment

@Module
class SearchFragmentModule(
        private val fragment: SearchFragment
) {

    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

}
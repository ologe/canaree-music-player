package dev.olog.presentation.fragment_search.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_search.SearchFragment
import dev.olog.presentation.fragment_search.SearchFragmentViewModel
import dev.olog.presentation.fragment_search.SearchFragmentViewModelFactory
import dev.olog.presentation.fragment_search.SearchType

@Module
class SearchFragmentModule(
        private val fragment: SearchFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun viewModel(factory: SearchFragmentViewModelFactory): SearchFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(SearchFragmentViewModel::class.java)
    }

    @Provides
    @PerFragment
    fun provideRecycledViewPool(): RecyclerView.RecycledViewPool {
        return RecyclerView.RecycledViewPool()
    }

    @Provides
    internal fun provideEnums() : Array<SearchType> {
        return SearchType.values()
    }

}
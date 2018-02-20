package dev.olog.msc.presentation.search.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.search.SearchFragmentViewModel
import dev.olog.msc.presentation.search.SearchFragmentViewModelFactory

@Module
class SearchFragmentModule(private val fragment: SearchFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun viewModel(factory: SearchFragmentViewModelFactory): SearchFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(SearchFragmentViewModel::class.java)
    }

    @Provides
    @PerFragment
    fun provideRecycledViewPool() = RecyclerView.RecycledViewPool()

}
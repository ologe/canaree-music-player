package dev.olog.msc.presentation.search.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.RecyclerView
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.search.SearchFragmentViewModel

@Module(includes = [SearchFragmentModule.Binding::class])
class SearchFragmentModule(private val fragment: SearchFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    @PerFragment
    fun provideRecycledViewPool() = RecyclerView.RecycledViewPool()

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(SearchFragmentViewModel::class)
        fun provideViewModel(factory: SearchFragmentViewModel): ViewModel

    }

}
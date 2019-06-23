package dev.olog.msc.presentation.search.di

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dagger.PerFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.search.SearchFragmentViewModel

@Module(includes = [SearchFragmentModule.Binding::class])
class SearchFragmentModule(private val fragment: SearchFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    @PerFragment
    fun provideRecycledViewPool() = androidx.recyclerview.widget.RecyclerView.RecycledViewPool()

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(SearchFragmentViewModel::class)
        fun provideViewModel(factory: SearchFragmentViewModel): ViewModel

    }

}
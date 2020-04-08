package dev.olog.presentation.search.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.presentation.search.SearchFragmentViewModel

@Module
abstract class SearchFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchFragmentViewModel::class)
    internal abstract fun provideViewModel(factory: SearchFragmentViewModel): ViewModel

}
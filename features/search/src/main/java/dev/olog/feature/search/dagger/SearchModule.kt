package dev.olog.feature.search.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.feature.search.SearchFragmentViewModel

@Module
abstract class SearchModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchFragmentViewModel::class)
    internal abstract fun provideViewModel(factory: SearchFragmentViewModel): ViewModel

}
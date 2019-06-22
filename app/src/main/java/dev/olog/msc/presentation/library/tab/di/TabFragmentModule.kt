package dev.olog.msc.presentation.library.tab.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.presentation.library.tab.TabFragmentViewModel

@Module
internal abstract class TabFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(TabFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: TabFragmentViewModel): ViewModel

}
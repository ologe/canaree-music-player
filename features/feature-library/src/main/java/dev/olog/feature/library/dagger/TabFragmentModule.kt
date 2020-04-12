package dev.olog.feature.library.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.feature.library.tab.TabFragmentViewModel

@Module
internal abstract class TabFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(TabFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: TabFragmentViewModel): ViewModel

}
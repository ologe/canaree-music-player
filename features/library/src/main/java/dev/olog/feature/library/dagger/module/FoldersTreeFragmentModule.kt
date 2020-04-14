package dev.olog.feature.library.dagger.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.library.folder.tree.FoldersTreeFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class FoldersTreeFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(FoldersTreeFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: FoldersTreeFragmentViewModel): ViewModel

}
package dev.olog.feature.library.dagger.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.library.folder.normal.FoldersNormalFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class FoldersNormalFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(FoldersNormalFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: FoldersNormalFragmentViewModel): ViewModel

}
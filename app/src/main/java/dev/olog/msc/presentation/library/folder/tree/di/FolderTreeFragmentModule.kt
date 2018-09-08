package dev.olog.msc.presentation.library.folder.tree.di

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragmentViewModel

@Module
internal abstract class FolderTreeFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(FolderTreeFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: FolderTreeFragmentViewModel): ViewModel

}
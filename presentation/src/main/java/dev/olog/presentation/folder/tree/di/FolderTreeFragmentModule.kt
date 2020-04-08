package dev.olog.presentation.folder.tree.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.presentation.folder.tree.FolderTreeFragment
import dev.olog.presentation.folder.tree.FolderTreeFragmentViewModel

@Module
abstract class FolderTreeFragmentModule {

    @ContributesAndroidInjector
    internal abstract fun provideFolderTreeFragment(): FolderTreeFragment

    @Binds
    @IntoMap
    @ViewModelKey(FolderTreeFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: FolderTreeFragmentViewModel): ViewModel

}
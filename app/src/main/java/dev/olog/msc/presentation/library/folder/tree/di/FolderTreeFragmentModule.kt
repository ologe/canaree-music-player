package dev.olog.msc.presentation.library.folder.tree.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragmentViewModel
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragmentViewModelFactory

@Module
class FolderTreeFragmentModule(private val fragment: FolderTreeFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

    @Provides
    fun provideViewModel(factory: FolderTreeFragmentViewModelFactory): FolderTreeFragmentViewModel{
        return ViewModelProviders.of(fragment, factory).get(FolderTreeFragmentViewModel::class.java)
    }

}
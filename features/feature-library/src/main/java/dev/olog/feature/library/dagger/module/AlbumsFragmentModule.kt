package dev.olog.feature.library.dagger.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.library.album.AlbumsFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class AlbumsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(AlbumsFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: AlbumsFragmentViewModel): ViewModel

}
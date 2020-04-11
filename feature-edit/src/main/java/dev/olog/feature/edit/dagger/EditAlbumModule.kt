package dev.olog.feature.edit.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.edit.EditItemViewModel
import dev.olog.feature.edit.album.EditAlbumFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
abstract class EditAlbumModule {

    @Binds
    @IntoMap
    @ViewModelKey(EditItemViewModel::class)
    internal abstract fun provideItemViewModel(viewModel: EditItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditAlbumFragmentViewModel::class)
    internal abstract fun provideAlbumViewModel(viewModel: EditAlbumFragmentViewModel): ViewModel

}
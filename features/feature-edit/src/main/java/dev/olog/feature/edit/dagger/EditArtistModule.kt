package dev.olog.feature.edit.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.edit.EditItemViewModel
import dev.olog.feature.edit.artist.EditArtistFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
abstract class EditArtistModule {

    @Binds
    @IntoMap
    @ViewModelKey(EditItemViewModel::class)
    internal abstract fun provideItemViewModel(viewModel: EditItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditArtistFragmentViewModel::class)
    internal abstract fun provideArtistViewModel(viewModel: EditArtistFragmentViewModel): ViewModel

}
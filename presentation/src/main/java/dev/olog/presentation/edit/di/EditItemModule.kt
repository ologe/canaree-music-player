package dev.olog.presentation.edit.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.edit.album.EditAlbumFragmentViewModel
import dev.olog.presentation.edit.artist.EditArtistFragmentViewModel
import dev.olog.presentation.edit.song.EditTrackFragmentViewModel


@Module
abstract class EditItemModule {

    @Binds
    @IntoMap
    @ViewModelKey(EditAlbumFragmentViewModel::class)
    internal abstract fun provideAlbumViewModel(viewModel: EditAlbumFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditArtistFragmentViewModel::class)
    internal abstract fun provideArtistViewModel(viewModel: EditArtistFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditTrackFragmentViewModel::class)
    internal abstract fun provideTrackViewModel(viewModel: EditTrackFragmentViewModel): ViewModel

}
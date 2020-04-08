package dev.olog.presentation.edit.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.album.EditAlbumFragment
import dev.olog.presentation.edit.album.EditAlbumFragmentViewModel
import dev.olog.presentation.edit.artist.EditArtistFragment
import dev.olog.presentation.edit.artist.EditArtistFragmentViewModel
import dev.olog.presentation.edit.song.EditTrackFragment
import dev.olog.presentation.edit.song.EditTrackFragmentViewModel


@Module
abstract class EditItemModule {

    @ContributesAndroidInjector
    internal abstract fun provideEditTrackFragment(): EditTrackFragment

    @ContributesAndroidInjector
    internal abstract fun provideEditAlbumFragment(): EditAlbumFragment

    @ContributesAndroidInjector
    internal abstract fun provideEditArtistFragment(): EditArtistFragment

    @Binds
    @IntoMap
    @ViewModelKey(EditItemViewModel::class)
    internal abstract fun provideItemViewModel(viewModel: EditItemViewModel): ViewModel

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
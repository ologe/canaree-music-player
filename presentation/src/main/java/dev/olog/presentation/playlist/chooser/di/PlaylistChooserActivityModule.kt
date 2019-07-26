package dev.olog.presentation.playlist.chooser.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.playlist.chooser.PlaylistChooserActivityViewModel

@Module
abstract class PlaylistChooserActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistChooserActivityViewModel::class)
    abstract fun provideViewModel(viewModel: PlaylistChooserActivityViewModel): ViewModel

}
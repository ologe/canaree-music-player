package dev.olog.feature.library.dagger.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.library.playlists.PlaylistsFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class PlaylistsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistsFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: PlaylistsFragmentViewModel): ViewModel

}
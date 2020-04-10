package dev.olog.feature.player.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.player.PlayerFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class PlayerFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayerFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: PlayerFragmentViewModel): ViewModel

}
package dev.olog.presentation.player.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.presentation.player.PlayerFragment
import dev.olog.presentation.player.PlayerFragmentViewModel
import dev.olog.presentation.player.volume.PlayerVolumeFragment

@Module
abstract class PlayerFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): PlayerFragment

    @ContributesAndroidInjector
    abstract fun provideVolumeFragment(): PlayerVolumeFragment

    @Binds
    @IntoMap
    @ViewModelKey(PlayerFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: PlayerFragmentViewModel): ViewModel

}
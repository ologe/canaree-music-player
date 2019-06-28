package dev.olog.presentation.player.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.player.PlayerFragment
import dev.olog.presentation.player.PlayerFragmentViewModel

@Module
abstract class PlayerFragmentModule {

    @ContributesAndroidInjector
    abstract fun proviewFragment(): PlayerFragment

    @Binds
    @IntoMap
    @ViewModelKey(PlayerFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: PlayerFragmentViewModel): ViewModel

}
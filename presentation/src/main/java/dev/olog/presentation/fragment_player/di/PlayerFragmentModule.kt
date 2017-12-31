package dev.olog.presentation.fragment_player.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.fragment_player.PlayerFragment
import dev.olog.presentation.fragment_player.PlayerFragmentViewModel
import dev.olog.presentation.fragment_player.PlayerFragmentViewModelFactory

@Module
class PlayerFragmentModule(
       private val fragment: PlayerFragment
) {

    @Provides
    internal fun provideViewModel(factory: PlayerFragmentViewModelFactory): PlayerFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(PlayerFragmentViewModel::class.java)
    }



}
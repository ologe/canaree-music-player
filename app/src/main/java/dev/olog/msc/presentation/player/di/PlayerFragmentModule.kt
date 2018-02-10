package dev.olog.msc.presentation.player.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.player.PlayerFragment
import dev.olog.msc.presentation.player.PlayerFragmentViewModel
import dev.olog.msc.presentation.player.PlayerFragmentViewModelFactory

@Module
class PlayerFragmentModule(
       private val fragment: PlayerFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideViewModel(factory: PlayerFragmentViewModelFactory): PlayerFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(PlayerFragmentViewModel::class.java)
    }



}
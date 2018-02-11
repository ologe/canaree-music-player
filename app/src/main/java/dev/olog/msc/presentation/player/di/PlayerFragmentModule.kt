package dev.olog.msc.presentation.player.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.player.PlayerFragment

@Module
class PlayerFragmentModule(
       private val fragment: PlayerFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

}
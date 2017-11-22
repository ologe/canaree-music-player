package dev.olog.presentation.fragment_player.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_player.PlayerFragment


@Subcomponent(modules = arrayOf(
        PlayerFragmentModule::class
))
@PerFragment
interface PlayerFragmentSubComponent : AndroidInjector<PlayerFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlayerFragment>() {

        abstract fun module(module: PlayerFragmentModule): Builder

        override fun seedInstance(instance: PlayerFragment) {
            module(PlayerFragmentModule(instance))
        }
    }

}
package dev.olog.presentation.fragment_mini_player.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragment

@Subcomponent(modules = arrayOf(MiniPlayerFragmentModule::class))
@PerFragment
interface MiniPlayerFragmentSubComponent : AndroidInjector<MiniPlayerFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MiniPlayerFragment>() {

        abstract fun module(module: MiniPlayerFragmentModule): Builder

        override fun seedInstance(instance: MiniPlayerFragment) {
            module(MiniPlayerFragmentModule(instance))
        }
    }

}

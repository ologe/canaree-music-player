package dev.olog.presentation.fragment_playing_queue.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_playing_queue.PlayingQueueFragment

@Subcomponent(modules = arrayOf(
        PlayingQueueFragmentModule::class
))
@PerFragment
interface PlayingQueueFragmentSubComponent : AndroidInjector<PlayingQueueFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlayingQueueFragment>() {

        abstract fun module(module: PlayingQueueFragmentModule): Builder

        override fun seedInstance(instance: PlayingQueueFragment) {
            module(PlayingQueueFragmentModule(instance))
        }
    }

}
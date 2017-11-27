package dev.olog.presentation.fragment_queue.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_queue.PlayingQueueFragment


@Subcomponent(modules = arrayOf(
        PlayingQueueModule::class
))
@PerFragment
interface PlayingQueueSubComponent : AndroidInjector<PlayingQueueFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlayingQueueFragment>() {

        abstract fun module(module: PlayingQueueModule): Builder

        override fun seedInstance(instance: PlayingQueueFragment) {
            module(PlayingQueueModule(instance))
        }
    }

}
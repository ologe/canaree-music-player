package dev.olog.presentation.queue.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.queue.PlayingQueueFragment

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
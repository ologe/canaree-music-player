package dev.olog.msc.presentation.playing.queue.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment

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
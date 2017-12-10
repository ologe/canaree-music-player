package dev.olog.presentation.fragment_mini_queue.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_mini_queue.MiniQueueFragment


@Subcomponent(modules = arrayOf(
        MiniQueueFragmentModule::class
))
@PerFragment
interface MiniQueueFragmentSubComponent : AndroidInjector<MiniQueueFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MiniQueueFragment>() {

        abstract fun module(module: MiniQueueFragmentModule): Builder

        override fun seedInstance(instance: MiniQueueFragment) {
            module(MiniQueueFragmentModule(instance))
        }
    }

}
package dev.olog.presentation.fragment_queue.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_queue.PlayingQueueFragment
import dev.olog.presentation.fragment_queue.PlayingQueueViewModel
import dev.olog.presentation.fragment_queue.PlayingQueueViewModelFactory

@Module
class PlayingQueueModule(
        private val fragment: PlayingQueueFragment
) {

    @Provides
    fun provideViewModel(factory: PlayingQueueViewModelFactory): PlayingQueueViewModel{
        return ViewModelProviders.of(fragment, factory).get(PlayingQueueViewModel::class.java)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

}
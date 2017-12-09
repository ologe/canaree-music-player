package dev.olog.presentation.fragment_queue.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_queue.PlayingQueueFragment
import dev.olog.presentation.fragment_queue.PlayingQueueFragmentViewModelFactory
import dev.olog.presentation.fragment_queue.PlayingQueueViewModel

@Module
class PlayingQueueFragmentModule(
        private val fragment: PlayingQueueFragment
) {

    @Provides
    fun provideViewModel(factory: PlayingQueueFragmentViewModelFactory): PlayingQueueViewModel{
        return ViewModelProviders.of(fragment, factory).get(PlayingQueueViewModel::class.java)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

}
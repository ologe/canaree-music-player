package dev.olog.presentation.fragment_playing_queue.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_playing_queue.PlayingQueueFragment
import dev.olog.presentation.fragment_playing_queue.PlayingQueueFragmentViewModel
import dev.olog.presentation.fragment_playing_queue.PlayingQueueFragmentViewModelFactory

@Module
class PlayingQueueFragmentModule(
        private val fragment: PlayingQueueFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

    @Provides
    fun provideViewModel(factory: PlayingQueueFragmentViewModelFactory): PlayingQueueFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(PlayingQueueFragmentViewModel::class.java)
    }

}
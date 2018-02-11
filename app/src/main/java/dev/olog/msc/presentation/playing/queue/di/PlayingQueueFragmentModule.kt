package dev.olog.msc.presentation.playing.queue.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragmentViewModel
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragmentViewModelFactory

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
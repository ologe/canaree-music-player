package dev.olog.presentation.fragment_mini_queue.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_mini_queue.MiniQueueFragment
import dev.olog.presentation.fragment_mini_queue.MiniQueueFragmentViewModelFactory
import dev.olog.presentation.fragment_mini_queue.MiniQueueViewModel

@Module
class MiniQueueFragmentModule(
        private val fragment: MiniQueueFragment
) {

    @Provides
    fun provideViewModel(factory: MiniQueueFragmentViewModelFactory): MiniQueueViewModel {
        return ViewModelProviders.of(fragment, factory).get(MiniQueueViewModel::class.java)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

}
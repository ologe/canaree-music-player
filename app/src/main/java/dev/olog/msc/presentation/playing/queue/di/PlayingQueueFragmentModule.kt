package dev.olog.msc.presentation.playing.queue.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragmentViewModel

@Module(includes = [PlayingQueueFragmentModule.Binding::class])
class PlayingQueueFragmentModule(
        private val fragment: PlayingQueueFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(PlayingQueueFragmentViewModel::class)
        fun provideViewModel(viewModel: PlayingQueueFragmentViewModel): ViewModel

    }

}
package dev.olog.presentation.queue.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.presentation.queue.PlayingQueueFragmentViewModel

@Module
abstract class PlayingQueueFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayingQueueFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: PlayingQueueFragmentViewModel): ViewModel

}
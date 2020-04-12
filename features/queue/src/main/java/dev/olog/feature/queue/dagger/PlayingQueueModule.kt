package dev.olog.feature.queue.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.feature.queue.PlayingQueueFragmentViewModel

@Module
internal abstract class PlayingQueueModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayingQueueFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: PlayingQueueFragmentViewModel): ViewModel

}

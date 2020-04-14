package dev.olog.feature.library.dagger.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.library.tracks.TracksFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class TracksFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(TracksFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: TracksFragmentViewModel): ViewModel

}
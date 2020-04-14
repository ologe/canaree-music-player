package dev.olog.feature.equalizer.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.equalizer.EqualizerFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class FragmentEqualizerModule {

    @Binds
    @IntoMap
    @ViewModelKey(EqualizerFragmentViewModel::class)
    internal abstract fun provideEditFragmentViewModel(viewModel: EqualizerFragmentViewModel): ViewModel

}
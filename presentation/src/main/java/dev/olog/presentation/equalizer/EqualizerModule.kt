package dev.olog.presentation.equalizer

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
abstract class EqualizerModule {

    @ContributesAndroidInjector
    internal abstract fun provideEqualizerFragment(): EqualizerFragment

    @Binds
    @IntoMap
    @ViewModelKey(EqualizerFragmentViewModel::class)
    internal abstract fun provideEditFragmentViewModel(viewModel: EqualizerFragmentViewModel): ViewModel

}
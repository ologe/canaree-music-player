package dev.olog.presentation.equalizer

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey

@Module
abstract class EqualizerModule {

    @ContributesAndroidInjector
    internal abstract fun provideEqualizerFragment(): EqualizerFragment

    @Binds
    @IntoMap
    @ViewModelKey(EqualizerFragmentPresenter::class)
    internal abstract fun provideViewModel(viewModel: EqualizerFragmentPresenter): ViewModel

}
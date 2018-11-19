package dev.olog.msc.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.presentation.detail.DetailFragmentViewModel

@Module
abstract class DetailFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel

}
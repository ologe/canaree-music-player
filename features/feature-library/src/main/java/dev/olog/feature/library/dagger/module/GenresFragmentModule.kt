package dev.olog.feature.library.dagger.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.library.genre.GenresFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class GenresFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(GenresFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: GenresFragmentViewModel): ViewModel

}
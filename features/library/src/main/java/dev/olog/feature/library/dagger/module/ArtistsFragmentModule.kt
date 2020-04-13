package dev.olog.feature.library.dagger.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.library.artist.ArtistsFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey

@Module
internal abstract class ArtistsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ArtistsFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: ArtistsFragmentViewModel): ViewModel

}
package dev.olog.presentation.fragment_mini_player.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragment
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragmentViewModel
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragmentViewModelFactory

@Module
class MiniPlayerFragmentModule(
        private val fragment: MiniPlayerFragment
) {

    @Provides
    internal fun provideViewModel(factory: MiniPlayerFragmentViewModelFactory): MiniPlayerFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(MiniPlayerFragmentViewModel::class.java)
    }

}
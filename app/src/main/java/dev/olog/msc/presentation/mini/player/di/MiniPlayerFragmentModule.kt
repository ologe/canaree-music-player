package dev.olog.msc.presentation.mini.player.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.mini.player.MiniPlayerFragmentViewModel
import dev.olog.msc.presentation.mini.player.MiniPlayerFragmentViewModelFactory
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragment

@Module
class MiniPlayerFragmentModule(
        private val fragment: MiniPlayerFragment
) {

    @Provides
    internal fun provideViewModel(factory: MiniPlayerFragmentViewModelFactory): MiniPlayerFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(MiniPlayerFragmentViewModel::class.java)
    }

}
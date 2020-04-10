package dev.olog.presentation.main.di

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.feature.presentation.base.activity.SharedViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.main.MainActivityViewModel

@Module
abstract class MainActivityModule {

    @Binds
    internal abstract fun provideFragmentActivity(instance: MainActivity): FragmentActivity

    @Binds
    internal abstract fun provideMusicGlue(instance: MainActivity): MediaProvider

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun provideViewModel(impl: MainActivityViewModel): ViewModel

}
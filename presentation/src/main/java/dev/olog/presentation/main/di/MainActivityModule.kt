package dev.olog.presentation.main.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.injection.dagger.ActivityContext
import dev.olog.media.MediaProvider
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.main.MainActivityViewModel
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.navigator.NavigatorImpl

@Module
abstract class MainActivityModule {

    @Binds
    @ActivityContext
    internal abstract fun provideContext(instance: MainActivity): Context

    @Binds
    internal abstract fun provideFragmentActivity(instance: MainActivity): FragmentActivity

    @Binds
    internal abstract fun provideMusicGlue(instance: MainActivity): MediaProvider

    @Binds
    @PerActivity
    internal abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun proviewViewModel(impl: MainActivityViewModel): ViewModel

}
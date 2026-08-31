package dev.olog.presentation.main.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.injection.dagger.ActivityContext
import dev.olog.media.MediaProvider
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.main.MainActivity
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
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Module
    companion object {

        @Provides
        @JvmStatic
        @ActivityLifecycle
        internal fun provideLifecycle(instance: MainActivity): Lifecycle = instance.lifecycle

    }

}
package dev.olog.presentation.di

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dev.olog.feature.media.api.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.navigator.NavigatorAboutImpl
import dev.olog.presentation.navigator.NavigatorImpl

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    @ActivityScoped
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @ActivityScoped
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

    companion object {

        @Provides
        @ActivityScoped
        fun provideMusicGlue(instance: FragmentActivity): MediaProvider {
            require(instance is MediaProvider)
            return instance
        }

    }

}
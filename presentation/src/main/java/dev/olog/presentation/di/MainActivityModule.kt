package dev.olog.presentation.di

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.media.MediaProvider
import dev.olog.shared.android.extensions.asType
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.navigator.NavigatorAboutImpl
import dev.olog.presentation.navigator.NavigatorImpl

@Module
@InstallIn(ActivityComponent::class)
abstract class MainActivityModule {

    @Binds
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

    companion object {

        @Provides
        fun provideMusicGlue(instance: FragmentActivity): MediaProvider {
            return instance.asType<MediaProvider>()
        }

    }

}
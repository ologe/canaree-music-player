package dev.olog.presentation.main.di

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.navigator.NavigatorImpl

@Module
@InstallIn(ActivityComponent::class)
abstract class MainActivityModule {

    @Binds
    abstract fun provideNavigator(impl: NavigatorImpl): Navigator

    companion object {
        @Provides
        internal fun provideMusicGlue(instance: FragmentActivity): MediaProvider {
            require(instance is MediaProvider)
            return instance
        }
    }

}
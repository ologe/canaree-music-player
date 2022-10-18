package dev.olog.msc.app

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.navigator.NavigatorAboutImpl
import dev.olog.presentation.navigator.NavigatorImpl
import dev.olog.presentation.pro.BillingMock
import dev.olog.presentation.pro.IBilling

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    @ActivityScoped
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @ActivityScoped
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

    @Binds
    @ActivityScoped
    internal abstract fun provideBilling(impl: BillingMock): IBilling

    companion object {

        @Provides
        fun provideMusicGlue(activity: FragmentActivity): MediaProvider {
            // todo replace with an extension?
            require(activity is MediaProvider)
            return activity
        }

    }

}
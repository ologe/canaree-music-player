package dev.olog.presentation.main.di

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.navigator.NavigatorImpl
import dev.olog.presentation.pro.BillingMock
import dev.olog.presentation.pro.IBilling
import dev.olog.shared.android.extensions.findInContext

@Module
@InstallIn(ActivityComponent::class)
abstract class MainActivityModule {

    companion object {
        @Provides
        internal fun provideMusicGlue(instance: FragmentActivity): MediaProvider {
            return instance.findInContext()
        }
    }

    @Binds
    @ActivityScoped
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @ActivityScoped
    internal abstract fun provideBilling(impl: BillingMock): IBilling

}
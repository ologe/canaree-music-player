package dev.olog.presentation.main.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
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
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

    @Binds
    @ActivityScoped
    internal abstract fun provideBilling(impl: BillingMock): IBilling

}
package dev.olog.navigation

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class NavigationModule {

    @Binds
    @Singleton
    internal abstract fun provideNavigator(impl: NavigatorImpl): Navigator

}
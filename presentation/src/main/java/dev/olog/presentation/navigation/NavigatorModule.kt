package dev.olog.presentation.navigation

import dagger.Binds
import dagger.Module
import dev.olog.presentation.dagger.PerActivity

@Module
abstract class NavigatorModule {

    @Binds
    @PerActivity
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

}

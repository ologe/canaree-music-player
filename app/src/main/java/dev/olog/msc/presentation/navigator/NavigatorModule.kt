package dev.olog.msc.presentation.navigator

import dagger.Binds
import dagger.Module
import dev.olog.msc.dagger.PerActivity

@Module
abstract class NavigatorModule {

    @Binds
    @PerActivity
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @PerActivity
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

}

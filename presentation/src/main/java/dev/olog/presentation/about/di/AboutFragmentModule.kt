package dev.olog.presentation.about.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.navigator.NavigatorAboutImpl

@Module
@InstallIn(ActivityComponent::class)
abstract class AboutFragmentModule {

    @Binds
    abstract fun provideNavigatorAbout(navigatorImpl: NavigatorAboutImpl): NavigatorAbout

}
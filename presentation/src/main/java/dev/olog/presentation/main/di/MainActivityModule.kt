package dev.olog.presentation.main.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.navigator.NavigatorImpl

@Module
@InstallIn(ActivityComponent::class)
abstract class MainActivityModule {

    @Binds
    abstract fun provideNavigator(impl: NavigatorImpl): NavigatorLegacy

}
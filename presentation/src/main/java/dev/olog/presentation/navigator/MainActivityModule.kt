package dev.olog.presentation.navigator

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.navigator.NavigatorLegacyImpl

@Module
@InstallIn(ActivityComponent::class)
@Deprecated("delete")
abstract class MainActivityModule {

    @Binds
    abstract fun provideNavigator(impl: NavigatorLegacyImpl): NavigatorLegacy

}
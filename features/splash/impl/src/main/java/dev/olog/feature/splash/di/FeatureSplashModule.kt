package dev.olog.feature.splash.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.splash.navigation.FeatureSplashNavigatorImpl
import dev.olog.feature.splash.api.FeatureSplashNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureSplashModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureSplashNavigatorImpl): FeatureSplashNavigator

}
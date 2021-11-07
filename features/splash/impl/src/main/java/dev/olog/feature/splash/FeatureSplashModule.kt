package dev.olog.feature.splash

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureSplashModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureSplashNavigatorImpl): FeatureSplashNavigator

}
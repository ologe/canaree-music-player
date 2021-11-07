package dev.olog.feature.floating

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureFloatingModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureFloatingNavigatorImpl): FeatureFloatingNavigator

}
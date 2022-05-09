package dev.olog.feature.main

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureMainModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureMainNavigatorImpl): FeatureMainNavigator

}
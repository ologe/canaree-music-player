package dev.olog.feature.detail

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureDetailModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureDetailNavigatorImpl): FeatureDetailNavigator

}
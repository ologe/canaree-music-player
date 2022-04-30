package dev.olog.feature.media

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureMediaModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureMediaNavigatorImpl): FeatureMediaNavigator

}
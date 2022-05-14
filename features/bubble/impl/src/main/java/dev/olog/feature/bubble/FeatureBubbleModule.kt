package dev.olog.feature.bubble

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureBubbleModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureBubbleNavigatorImpl): FeatureBubbleNavigator

}
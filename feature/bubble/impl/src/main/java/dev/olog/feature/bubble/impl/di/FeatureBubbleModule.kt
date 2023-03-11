package dev.olog.feature.bubble.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.bubble.impl.navigation.FeatureBubbleNavigatorImpl

@Module
@InstallIn(SingletonComponent::class)
interface FeatureBubbleModule {

    @Binds
    fun provideNavigator(impl: FeatureBubbleNavigatorImpl): FeatureBubbleNavigator

}
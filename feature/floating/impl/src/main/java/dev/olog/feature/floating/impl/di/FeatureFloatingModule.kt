package dev.olog.feature.floating.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.floating.api.FeatureFloatingNavigator
import dev.olog.feature.floating.impl.FeatureFloatingNavigatorImpl

@Module
@InstallIn(SingletonComponent::class)
interface FeatureFloatingModule {

    @Binds
    fun provideNavigator(impl: FeatureFloatingNavigatorImpl): FeatureFloatingNavigator

}
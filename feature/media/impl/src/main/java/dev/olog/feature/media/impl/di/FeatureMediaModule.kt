package dev.olog.feature.media.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.impl.navigator.FeatureMediaNavigatorImpl

@Module
@InstallIn(SingletonComponent::class)
interface FeatureMediaModule {

    @Binds
    fun provideNavigator(impl: FeatureMediaNavigatorImpl): FeatureMediaNavigator

}
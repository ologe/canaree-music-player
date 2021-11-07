package dev.olog.feature.offline.lyrics

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureOfflineLyricsModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureOfflineLyricsNavigatorImpl): FeatureOfflineLyricsNavigator

}
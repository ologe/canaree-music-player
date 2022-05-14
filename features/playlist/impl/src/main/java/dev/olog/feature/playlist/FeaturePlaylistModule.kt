package dev.olog.feature.playlist

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.playlist.api.FeaturePlaylistNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeaturePlaylistModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeaturePlaylistNavigatorImpl): FeaturePlaylistNavigator

}
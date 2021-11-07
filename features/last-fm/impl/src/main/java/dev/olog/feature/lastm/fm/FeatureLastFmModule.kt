package dev.olog.feature.lastm.fm

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureLastFmModule {

    @Binds
    @Singleton
    abstract fun providePrefs(impl: LastFmPrefsImpl): LastFmPrefs

}
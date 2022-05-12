package dev.olog.feature.player

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeaturePlayerModule {

    @Binds
    @Singleton
    abstract fun providePreferences(impl: PlayerPreferencesImpl): PlayerPreferences

}
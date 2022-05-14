package dev.olog.feature.media.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.Resettable
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.FeatureMediaNavigatorImpl
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.feature.media.MusicPreferencesImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureMediaModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureMediaNavigatorImpl): FeatureMediaNavigator

    @Binds
    @Singleton
    abstract fun providePrefs(impl: MusicPreferencesImpl): MusicPreferencesGateway

    @Binds
    @IntoSet
    abstract fun providePrefsResettable(impl: MusicPreferencesGateway): Resettable

}
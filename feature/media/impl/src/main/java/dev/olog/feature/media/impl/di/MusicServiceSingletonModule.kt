package dev.olog.feature.media.impl.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.api.MediaExposer
import dev.olog.feature.media.impl.FeatureMediaNavigatorImpl
import dev.olog.feature.media.impl.exposer.MediaExposerImpl

@Module
@InstallIn(SingletonComponent::class)
interface MusicServiceSingletonModule {

    @Binds
    fun provideNavigator(impl: FeatureMediaNavigatorImpl): FeatureMediaNavigator

    companion object {
        @Provides
        fun provideMediaExposerFactory(): MediaExposer.Factory {
            return MediaExposerImpl.Factory()
        }
    }

}
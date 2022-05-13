package dev.olog.feature.lyrics.offline

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.lyrics.offline.base.LyricsOfflinePresenterImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureLyricsOfflineModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureLyricsOfflineNavigatorImpl): FeatureLyricsOfflineNavigator

    @Binds
    // do not scope
    abstract fun providePresenter(impl: LyricsOfflinePresenterImpl): LyricsOfflinePresenter

}
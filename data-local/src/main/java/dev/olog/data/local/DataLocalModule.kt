package dev.olog.data.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.core.gateway.*
import dev.olog.data.local.equalizer.preset.EqualizerRepository
import dev.olog.data.local.favorite.FavoriteRepository
import dev.olog.data.local.lyrics.OfflineLyricsRepository
import dev.olog.data.local.playing.queue.PlayingQueueRepository
import dev.olog.data.local.search.RecentSearchesRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataLocalModule {

    @Binds
    @Singleton
    internal abstract fun providePlayingQueueRepository(repository: PlayingQueueRepository): PlayingQueueGateway

    @Binds
    @Singleton
    internal abstract fun provideFavoriteRepository(repository: FavoriteRepository): FavoriteGateway

    @Binds
    @Singleton
    internal abstract fun provideRecentSearchesRepository(repository: RecentSearchesRepository): RecentSearchesGateway

    @Binds
    @Singleton
    internal abstract fun provideLyricsRepository(repository: OfflineLyricsRepository): OfflineLyricsGateway

    @Binds
    @Singleton
    internal abstract fun provideEqualizerRepository(repository: EqualizerRepository): EqualizerGateway

}
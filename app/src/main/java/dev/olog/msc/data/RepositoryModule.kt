package dev.olog.msc.data

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.msc.data.repository.FavoriteRepository
import dev.olog.msc.data.repository.PlayingQueueRepository
import dev.olog.msc.data.repository.RecentSearchesRepository
import dev.olog.msc.data.repository.UsedImageRepository
import dev.olog.msc.data.repository.last.fm.LastFmRepository
import dev.olog.msc.data.repository.lyrics.OfflineLyricsRepository
import dev.olog.core.gateway.LastFmGateway
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.gateway.UsedImageGateway
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun providePlayingQueueRepository(repository: PlayingQueueRepository): PlayingQueueGateway

    @Binds
    @Singleton
    abstract fun provideFavoriteRepository(repository: FavoriteRepository): FavoriteGateway

    @Binds
    @Singleton
    abstract fun provideRecentSearchesRepository(repository: RecentSearchesRepository): RecentSearchesGateway

    @Binds
    @Singleton
    abstract fun provideLastFmRepository(repository: LastFmRepository): LastFmGateway

    @Binds
    @Singleton
    abstract fun provideLyricsRepository(repository: OfflineLyricsRepository): OfflineLyricsGateway

    @Binds
    @Singleton
    abstract fun provideUsedImageRepository(repository: UsedImageRepository): UsedImageGateway

}
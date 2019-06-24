package dev.olog.data

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.*
import dev.olog.data.repository.*
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
    abstract fun provideLyricsRepository(repository: OfflineLyricsRepository): OfflineLyricsGateway

    @Binds
    @Singleton
    abstract fun provideUsedImageRepository(repository: UsedImageRepository): UsedImageGateway

}
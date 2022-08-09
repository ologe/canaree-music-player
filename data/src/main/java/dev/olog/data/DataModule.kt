package dev.olog.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.data.repository.EqualizerRepository
import dev.olog.data.repository.FavoriteRepository
import dev.olog.data.repository.OfflineLyricsRepository
import dev.olog.data.repository.PlayingQueueRepository
import dev.olog.data.repository.RecentSearchesRepository
import dev.olog.data.repository.lastfm.ImageRetrieverRepository
import dev.olog.data.repository.podcast.PodcastAlbumRepository
import dev.olog.data.repository.podcast.PodcastArtistRepository
import dev.olog.data.repository.podcast.PodcastPlaylistRepository
import dev.olog.data.repository.podcast.PodcastRepository
import dev.olog.feature.equalizer.api.EqualizerGateway
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // podcasts
    @Binds
    @Singleton
    internal abstract fun providePodcastPlaylistRepository(repository: PodcastPlaylistRepository): PodcastPlaylistGateway

    @Binds
    @Singleton
    internal abstract fun providePodcsatRepository(repository: PodcastRepository): PodcastGateway
    @Binds
    @Singleton
    internal abstract fun providePodcastAlbumRepository(repository: PodcastAlbumRepository): PodcastAlbumGateway
    @Binds
    @Singleton
    internal abstract fun providePodcastArtistRepository(repository: PodcastArtistRepository): PodcastArtistGateway

    // other

    @Binds
    @Singleton
    internal abstract fun provideLastFmRepository(repository: ImageRetrieverRepository): ImageRetrieverGateway

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
    internal abstract fun provideEqualzierRepository(repository: EqualizerRepository): EqualizerGateway

}
package dev.olog.data.di

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.*
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.data.repository.*
import dev.olog.data.repository.lastfm.ImageRetrieverRepository
import dev.olog.data.repository.podcast.PodcastAuthorRepository
import dev.olog.data.repository.podcast.PodcastPlaylistRepository
import dev.olog.data.repository.podcast.PodcastRepository
import dev.olog.data.repository.track.*
import javax.inject.Singleton

@Module(includes = [QueriesModule::class])
abstract class DataModule {

    // tracks

    @Binds
    @Singleton
    internal abstract fun provideFolderRepository(repository: FolderRepository): FolderGateway

    @Binds
    @Singleton
    internal abstract fun providePlaylistRepository(repository: PlaylistRepository): PlaylistGateway

    @Binds
    @Singleton
    internal abstract fun provideSongRepository(repository: TrackRepository): TrackGateway

    @Binds
    @Singleton
    internal abstract fun provideAlbumRepository(repository: AlbumRepository): AlbumGateway

    @Binds
    @Singleton
    internal abstract fun provideArtistRepository(repository: ArtistRepository): ArtistGateway

    @Binds
    @Singleton
    internal abstract fun provideGenreRepository(repository: GenreRepository): GenreGateway

    // podcasts
    @Binds
    @Singleton
    internal abstract fun providePodcastPlaylistRepository(repository: PodcastPlaylistRepository): PodcastPlaylistGateway

    @Binds
    @Singleton
    internal abstract fun providePodcsatRepository(repository: PodcastRepository): PodcastGateway

    @Binds
    @Singleton
    internal abstract fun providePodcastArtistRepository(repository: PodcastAuthorRepository): PodcastAuthorGateway

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
    internal abstract fun provideFolderNavigator(repository: FolderNavigatorRepository): FolderNavigatorGateway

    @Binds
    @Singleton
    internal abstract fun provideEqualzierRepository(repository: EqualizerRepository): EqualizerGateway

}
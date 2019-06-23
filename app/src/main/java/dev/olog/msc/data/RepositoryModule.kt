package dev.olog.msc.data

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.msc.data.repository.*
import dev.olog.msc.data.repository.last.fm.LastFmRepository
import dev.olog.msc.data.repository.lyrics.OfflineLyricsRepository
import dev.olog.msc.data.repository.podcast.PlaylistPodcastRepository
import dev.olog.msc.data.repository.podcast.PodcastAlbumRepository
import dev.olog.msc.data.repository.podcast.PodcastArtistRepository
import dev.olog.msc.data.repository.podcast.PodcastRepository
import dev.olog.msc.domain.gateway.*
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideFolderRepository(repository: FolderRepository): FolderGateway

    @Binds
    @Singleton
    abstract fun providePlaylistRepository(repository: PlaylistRepository): PlaylistGateway

    @Binds
    @Singleton
    abstract fun provideSongRepository(repository: SongRepository): SongGateway

    @Binds
    @Singleton
    abstract fun provideAlbumRepository(repository: AlbumRepository): AlbumGateway

    @Binds
    @Singleton
    abstract fun provideArtistRepository(repository: ArtistRepository): ArtistGateway

    @Binds
    @Singleton
    abstract fun provideGenreRepository(repository: GenreRepository): GenreGateway

    @Binds
    @Singleton
    abstract fun providePodcastRepository(repository: PodcastRepository): PodcastGateway

    @Binds
    @Singleton
    abstract fun providePodcastPlaylistRepository(repository: PlaylistPodcastRepository): PodcastPlaylistGateway

    @Binds
    @Singleton
    abstract fun providePodcastAlbumsRepository(repository: PodcastAlbumRepository): PodcastAlbumGateway

    @Binds
    @Singleton
    abstract fun providePodcastArtistsRepository(repository: PodcastArtistRepository): PodcastArtistGateway

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
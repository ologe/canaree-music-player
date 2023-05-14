package dev.olog.data

import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.AppInitializer
import dev.olog.core.gateway.*
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.*
import dev.olog.data.blacklist.BlacklistRepository
import dev.olog.data.mediastore.MediaStoreAudioRepository
import dev.olog.data.playlist.FilePlaylistOperations
import dev.olog.data.playlist.MediaStorePlaylistOperations
import dev.olog.data.playlist.PlaylistOperations
import dev.olog.data.repository.*
import dev.olog.data.repository.lastfm.ImageRetrieverRepository
import dev.olog.data.repository.podcast.PodcastAlbumRepository
import dev.olog.data.repository.podcast.PodcastArtistRepository
import dev.olog.data.repository.podcast.PodcastRepository
import dev.olog.data.repository.track.*
import dev.olog.platform.BuildVersion
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @IntoSet
    abstract fun provideMediaStoreInitializer(impl: MediaStoreAudioRepository): AppInitializer

    @Binds
    @Singleton
    abstract fun provideBlacklistRepository(impl: BlacklistRepository): BlacklistGateway

    // tracks

    @Binds
    @Singleton
    internal abstract fun provideFolderRepository(repository: FolderRepository): FolderGateway

    @Binds
    @Singleton
    internal abstract fun providePlaylistRepository(repository: PlaylistRepository): PlaylistGateway

    @Binds
    @Singleton
    internal abstract fun provideAutoPlaylistRepository(repository: AutoPlaylistRepository): AutoPlaylistGateway

    @Binds
    @Singleton
    internal abstract fun provideSongRepository(repository: SongRepository): SongGateway

    @Binds
    @Singleton
    internal abstract fun provideAlbumRepository(repository: AlbumRepository): AlbumGateway

    @Binds
    @Singleton
    internal abstract fun provideArtistRepository(repository: ArtistRepository): ArtistGateway

    @Binds
    @Singleton
    internal abstract fun provideGenreRepository(repository: GenreRepository): GenreGateway

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

    companion object {

        @Provides
        @Singleton
        fun providePlaylistOperations(
            filePlaylistOperations: Lazy<FilePlaylistOperations>,
            mediaStorePlaylistOperations: Lazy<MediaStorePlaylistOperations>,
        ): PlaylistOperations = when {
            // android 10 introduced scoped storage and completely broke playlists
            BuildVersion.isQ() -> filePlaylistOperations.get()
            else -> mediaStorePlaylistOperations.get()
        }

    }

}
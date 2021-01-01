package dev.olog.data.mediastore

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.domain.gateway.FolderNavigatorGateway
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.data.mediastore.repository.FolderNavigatorRepository
import dev.olog.data.mediastore.repository.lastfm.ImageRetrieverRepository
import dev.olog.data.mediastore.repository.podcast.PodcastAlbumRepository
import dev.olog.data.mediastore.repository.podcast.PodcastArtistRepository
import dev.olog.data.mediastore.repository.podcast.PodcastPlaylistRepository
import dev.olog.data.mediastore.repository.podcast.PodcastRepository
import dev.olog.data.mediastore.repository.track.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class DataModule {

    // tracks

    @Binds
    @Singleton
    internal abstract fun provideFolderRepository(repository: FolderRepository): FolderGateway

    @Binds
    @Singleton
    internal abstract fun providePlaylistRepository(repository: PlaylistRepository): PlaylistGateway

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
    internal abstract fun provideFolderNavigator(repository: FolderNavigatorRepository): FolderNavigatorGateway


}
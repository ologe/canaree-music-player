package dev.olog.data

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.*
import dev.olog.data.repository.*
import dev.olog.data.repository.podcast.PodcastAlbumRepository2
import dev.olog.data.repository.podcast.PodcastArtistRepository2
import dev.olog.data.repository.podcast.PodcastPlaylistRepository2
import dev.olog.data.repository.podcast.PodcastRepository2
import javax.inject.Singleton

@Module
abstract class DataModule {


    // tracks

    @Binds
    @Singleton
    internal abstract fun provideFolderRepository(repository: FolderRepository2): FolderGateway

    @Binds
    @Singleton
    internal abstract fun providePlaylistRepository(repository: PlaylistRepository2): PlaylistGateway2

    @Binds
    @Singleton
    internal abstract fun provideSongRepository(repository: SongRepository2): SongGateway

    @Binds
    @Singleton
    internal abstract fun provideAlbumRepository(repository: AlbumRepository2): AlbumGateway

    @Binds
    @Singleton
    internal abstract fun provideArtistRepository(repository: ArtistRepository2): ArtistGateway

    @Binds
    @Singleton
    internal abstract fun provideGenreRepository(repository: GenreRepository2): GenreGateway

    // podcasts
    @Binds
    @Singleton
    internal abstract fun providePodcastPlaylistRepository(repository: PodcastPlaylistRepository2): PodcastPlaylistGateway

    @Binds
    @Singleton
    internal abstract fun providePodcsatRepository(repository: PodcastRepository2): PodcastGateway
    @Binds
    @Singleton
    internal abstract fun providePodcastAlbumRepository(repository: PodcastAlbumRepository2): PodcastAlbumGateway
    @Binds
    @Singleton
    internal abstract fun providePodcastArtistRepository(repository: PodcastArtistRepository2): PodcastArtistGateway

    // other

    @Binds
    @Singleton
    internal abstract fun provideLastFmRepository(repository: LastFmRepository2): LastFmGateway

}
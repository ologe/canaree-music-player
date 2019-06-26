package dev.olog.data

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.*
import dev.olog.data.repository.lastfm.LastFmRepository
import dev.olog.data.repository.podcast.PodcastAlbumRepository
import dev.olog.data.repository.podcast.PodcastArtistRepository
import dev.olog.data.repository.podcast.PodcastPlaylistRepository
import dev.olog.data.repository.podcast.PodcastRepository
import dev.olog.data.repository.track.AlbumRepository
import dev.olog.data.repository.track.ArtistRepository
import dev.olog.data.repository.track.FolderRepository
import dev.olog.data.repository.track.GenreRepository
import dev.olog.data.repository.track.PlaylistRepository
import dev.olog.data.repository.track.SongRepository
import javax.inject.Singleton

@Module
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
    internal abstract fun provideLastFmRepository(repository: LastFmRepository): LastFmGateway

}
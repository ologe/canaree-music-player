package dev.olog.data

import dagger.Binds
import dagger.Module
import dev.olog.data.repository.*
import dev.olog.domain.gateway.*
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
    abstract fun providePlayingQueueRepository(repository: PlayingQueueRepository): PlayingQueueGateway

    @Binds
    @Singleton
    abstract fun provideFavoriteRepository(repository: FavoriteRepository): FavoriteGateway

    @Binds
    @Singleton
    abstract fun provideRecentSearchesRepository(repository: RecentSearchesRepository): RecentSearchesGateway
}
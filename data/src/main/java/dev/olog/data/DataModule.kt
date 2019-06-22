package dev.olog.data

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.*
import dev.olog.data.repository.*
import javax.inject.Singleton

@Module
abstract class DataModule {

    @Binds
    @Singleton
    internal abstract fun provideFolderRepository(repository: FolderRepository2): FolderGateway2

    @Binds
    @Singleton
    internal abstract fun providePlaylistRepository(repository: PlaylistRepository2): PlaylistGateway2

    @Binds
    @Singleton
    internal abstract fun provideSongRepository(repository: SongRepository2): SongGateway2

    @Binds
    @Singleton
    internal abstract fun provideAlbumRepository(repository: AlbumRepository2): AlbumGateway2

    @Binds
    @Singleton
    internal abstract fun provideArtistRepository(repository: ArtistRepository2): ArtistGateway2

    @Binds
    @Singleton
    internal abstract fun provideGenreRepository(repository: GenreRepository2): GenreGateway2

}
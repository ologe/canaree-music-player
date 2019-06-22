package dev.olog.data

import dagger.Binds
import dagger.Module
import dev.olog.core.gateway.FolderGateway2
import dev.olog.core.gateway.GenreGateway2
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.core.gateway.SongGateway2
import dev.olog.data.repository.FolderRepository2
import dev.olog.data.repository.GenreRepository2
import dev.olog.data.repository.PlaylistRepository2
import dev.olog.data.repository.SongRepository2
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

//    @Binds
//    @Singleton
//    internal abstract fun provideAlbumRepository(repository: AlbumRepository): AlbumGateway

//    @Binds
//    @Singleton
//    internal abstract fun provideArtistRepository(repository: ArtistRepository): ArtistGateway

    @Binds
    @Singleton
    internal abstract fun provideGenreRepository(repository: GenreRepository2): GenreGateway2

}
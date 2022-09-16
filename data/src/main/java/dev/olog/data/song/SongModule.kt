package dev.olog.data.song

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.db.AppDatabase
import dev.olog.data.song.album.AlbumRepository
import dev.olog.data.song.artist.ArtistRepository
import dev.olog.data.song.folder.FolderRepository
import dev.olog.data.song.genre.GenreRepository
import dev.olog.data.song.playlist.PlaylistRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SongModule {

    @Binds
    @Singleton
    internal abstract fun provideFolderRepository(repository: FolderRepository): FolderGateway

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
    internal abstract fun providePlaylistRepository(repository: PlaylistRepository): PlaylistGateway

    companion object {

        @Provides
        fun provideSongDao(db: AppDatabase) = db.songDao()
        @Provides
        fun provideArtistDao(db: AppDatabase) = db.artistDao()
        @Provides
        fun provideAlbumDao(db: AppDatabase) = db.albumDao()
        @Provides
        fun provideFolderDao(db: AppDatabase) = db.folderDao()
        @Provides
        fun provideGenreDao(db: AppDatabase) = db.genreDao()
        @Provides
        fun providePlaylistDao(db: AppDatabase) = db.playlistDao()
    }

}
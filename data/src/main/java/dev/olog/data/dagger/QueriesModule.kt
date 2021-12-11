package dev.olog.data.dagger

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.data.Database

@Module
@InstallIn(SingletonComponent::class)
internal class QueriesModule {

    // region indexed

    @Provides
    fun provideIndexedPlayables(db: Database) = db.indexedPlayablesQueries

    @Provides
    fun provideIndexedGenres(db: Database) = db.indexedGenresQueries

    @Provides
    fun provideIndexedPlaylists(db: Database) = db.indexedPlaylistsQueries

    // endregion

    @Provides
    fun provideBlacklist(db: Database) = db.blacklistQueries

    @Provides
    fun provideSort(db: Database) = db.sortQueries

    @Provides
    fun provideSongs(db: Database) = db.songsQueries

    @Provides
    fun providePodcastEpisodes(db: Database) = db.podcastEpisodesQueries

    @Provides
    fun provideArtists(db: Database) = db.artistsQueries

    @Provides
    fun providePodcastAuthor(db: Database) = db.podcastAuthorsQueries

    @Provides
    fun provideAlbums(db: Database) = db.albumsQueries

    @Provides
    fun providePodcastCollections(db: Database) = db.podcastCollectionQueries

    @Provides
    fun provideFolders(db: Database) = db.foldersQueries

    @Provides
    fun provideGenres(db: Database) = db.genresQueries

    @Provides
    fun providePlaylists(db: Database) = db.playlistsQueries

    @Provides
    fun providePodcastPlaylists(db: Database) = db.podcastPlaylistsQueries

    @Provides
    fun provideFavorites(db: Database) = db.favoritesQueries

    @Provides
    fun provideLastAdded(db: Database) = db.lastAddedQueries

    @Provides
    fun provideHistory(db: Database) = db.historyQueries

    @Provides
    fun providePlayingItem(db: Database) = db.playingItemQueries

}
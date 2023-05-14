package dev.olog.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.data.blacklist.BlacklistDao
import dev.olog.data.db.AppDatabase
import dev.olog.data.db.dao.*
import dev.olog.data.mediastore.MediaStoreAudioInternalDao
import dev.olog.data.mediastore.album.MediaStoreAlbumDao
import dev.olog.data.mediastore.artist.MediaStoreArtistDao
import dev.olog.data.mediastore.audio.MediaStoreAudioDao
import dev.olog.data.mediastore.folder.MediaStoreFolderDao
import dev.olog.data.mediastore.genre.MediaStoreGenreDao
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDao

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    internal fun provideMediaStoreAudioInternalDao(db: AppDatabase): MediaStoreAudioInternalDao {
        return db.mediaStoreAudioInternalDao()
    }

    @Provides
    internal fun provideMediaStoreAudioDao(db: AppDatabase): MediaStoreAudioDao {
        return db.mediaStoreAudioDao()
    }

    @Provides
    internal fun provideMediaStoreFolderDao(db: AppDatabase): MediaStoreFolderDao {
        return db.mediaStoreFolderDao()
    }

    @Provides
    internal fun provideMediaStoreArtistDao(db: AppDatabase): MediaStoreArtistDao {
        return db.mediaStoreArtistDao()
    }

    @Provides
    internal fun provideMediaStoreAlbumDao(db: AppDatabase): MediaStoreAlbumDao {
        return db.mediaStoreAlbumDao()
    }

    @Provides
    internal fun provideMediaStoreGenreDao(db: AppDatabase): MediaStoreGenreDao {
        return db.mediaStoreGenreDao()
    }

    @Provides
    internal fun provideMediaStorePlaylistDao(db: AppDatabase): MediaStorePlaylistDao {
        return db.mediaStorePlaylistDao()
    }

    @Provides
    internal fun provideBlacklistDao(db: AppDatabase): BlacklistDao {
        return db.blacklistDao()
    }

    @Provides
    internal fun provideFavoritedDao(db: AppDatabase): FavoriteDao {
        return db.favoriteDao()
    }

    @Provides
    internal fun provideLastFmDao(db: AppDatabase): LastFmDao {
        return db.lastFmDao()
    }

    @Provides
    internal fun provideEqualizerPresetDao(db: AppDatabase): EqualizerPresetsDao {
        return db.equalizerPresetsDao()
    }

    @Provides
    internal fun provideOfflineLyricsDao(db: AppDatabase): OfflineLyricsDao {
        return db.offlineLyricsDao()
    }

    @Provides
    internal fun provideLyricsSyncDao(db: AppDatabase): LyricsSyncAdjustmentDao {
        return db.lyricsSyncAdjustmentDao()
    }

    @Provides
    internal fun providePlayingQueueDao(db: AppDatabase): PlayingQueueDao {
        return db.playingQueueDao()
    }

    @Provides
    internal fun provideHistoryDao(db: AppDatabase): HistoryDao {
        return db.historyDao()
    }

    @Provides
    internal fun provideRecentDao(db: AppDatabase): RecentSearchesDao {
        return db.recentSearchesDao()
    }

    @Provides
    internal fun provideLastPlayedAlbumsDao(db: AppDatabase): LastPlayedAlbumDao {
        return db.lastPlayedAlbumDao()
    }

    @Provides
    internal fun provideLastPlayedPodcastAlbumsDao(db: AppDatabase): LastPlayedPodcastAlbumDao {
        return db.lastPlayedPodcastAlbumDao()
    }

    @Provides
    internal fun provideLastPlayedArtistsDao(db: AppDatabase): LastPlayedArtistDao {
        return db.lastPlayedArtistDao()
    }

    @Provides
    internal fun provideLastPlayedPodcastArtistsDao(db: AppDatabase): LastPlayedPodcastArtistDao {
        return db.lastPlayedPodcastArtistDao()
    }

    @Provides
    internal fun provideFolderMostPlayedDao(db: AppDatabase): FolderMostPlayedDao {
        return db.folderMostPlayedDao()
    }

    @Provides
    internal fun providePlaylistMostPlayedDao(db: AppDatabase): PlaylistMostPlayedDao {
        return db.playlistMostPlayedDao()
    }

    @Provides
    internal fun provideGenreMostPlayedDao(db: AppDatabase): GenreMostPlayedDao {
        return db.genreMostPlayedDao()
    }

    @Provides
    internal fun providePodcastPositionDao(db: AppDatabase): PodcastPositionDao {
        return db.podcastPositionDao()
    }

}
package dev.olog.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.data.db.*
import dev.olog.data.spotify.db.SpotifyImagesDao

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {

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
    internal fun providePlaylistDao(db: AppDatabase): PlaylistDao {
        return db.playlistDao()
    }

    @Provides
    internal fun providePodcastPlaylistDao(db: AppDatabase): PodcastPlaylistDao {
        return db.podcastPlaylistDao()
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

    @Provides
    internal fun provideSpotifyImagesDao(db: AppDatabase): SpotifyImagesDao {
        return db.spotifyImagesDao()
    }

}
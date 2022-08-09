package dev.olog.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.data.db.AppDatabase
import dev.olog.data.db.equalizer.EqualizerPresetsDao
import dev.olog.data.db.favorite.FavoriteDao
import dev.olog.data.db.history.HistoryDao
import dev.olog.data.db.last.played.LastPlayedPodcastAlbumDao
import dev.olog.data.db.last.played.LastPlayedPodcastArtistDao
import dev.olog.data.db.lastfm.LastFmDao
import dev.olog.data.db.lyrics.LyricsSyncAdjustmentDao
import dev.olog.data.db.lyrics.OfflineLyricsDao
import dev.olog.data.db.most.played.PlaylistMostPlayedDao
import dev.olog.data.db.playlist.PlaylistDao
import dev.olog.data.db.playlist.PodcastPlaylistDao
import dev.olog.data.db.podcast.PodcastPositionDao
import dev.olog.data.db.queue.PlayingQueueDao
import dev.olog.data.db.recent.search.RecentSearchesDao

@Module
@InstallIn(SingletonComponent::class)
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
    internal fun provideLastPlayedPodcastAlbumsDao(db: AppDatabase): LastPlayedPodcastAlbumDao {
        return db.lastPlayedPodcastAlbumDao()
    }

    @Provides
    internal fun provideLastPlayedPodcastArtistsDao(db: AppDatabase): LastPlayedPodcastArtistDao {
        return db.lastPlayedPodcastArtistDao()
    }

    @Provides
    internal fun providePlaylistMostPlayedDao(db: AppDatabase): PlaylistMostPlayedDao {
        return db.playlistMostPlayedDao()
    }

    @Provides
    internal fun providePodcastPositionDao(db: AppDatabase): PodcastPositionDao {
        return db.podcastPositionDao()
    }

}
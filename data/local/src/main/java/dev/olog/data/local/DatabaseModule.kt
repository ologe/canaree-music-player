package dev.olog.data.local

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.data.local.AppDatabase
import dev.olog.data.local.equalizer.preset.EqualizerPresetsDao
import dev.olog.data.local.favorite.FavoriteDao
import dev.olog.data.local.history.HistoryDao
import dev.olog.data.local.last.fm.LastFmDao
import dev.olog.data.local.lyrics.OfflineLyricsDao
import dev.olog.data.local.lyrics.sync.LyricsSyncAdjustmentDao
import dev.olog.data.local.most.played.FolderMostPlayedDao
import dev.olog.data.local.most.played.GenreMostPlayedDao
import dev.olog.data.local.most.played.PlaylistMostPlayedDao
import dev.olog.data.local.playing.queue.PlayingQueueDao
import dev.olog.data.local.playlist.PlaylistDao
import dev.olog.data.local.playlist.PodcastPlaylistDao
import dev.olog.data.local.podcast.PodcastPositionDao
import dev.olog.data.local.recently.played.RecentlyPlayedAlbumDao
import dev.olog.data.local.recently.played.RecentlyPlayedArtistDao
import dev.olog.data.local.recently.played.RecentlyPlayedPodcastAlbumDao
import dev.olog.data.local.recently.played.RecentlyPlayedPodcastArtistDao
import dev.olog.data.local.search.RecentSearchesDao

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

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
    internal fun provideLastPlayedAlbumsDao(db: AppDatabase): RecentlyPlayedAlbumDao {
        return db.lastPlayedAlbumDao()
    }

    @Provides
    internal fun provideLastPlayedPodcastAlbumsDao(db: AppDatabase): RecentlyPlayedPodcastAlbumDao {
        return db.lastPlayedPodcastAlbumDao()
    }

    @Provides
    internal fun provideLastPlayedArtistsDao(db: AppDatabase): RecentlyPlayedArtistDao {
        return db.lastPlayedArtistDao()
    }

    @Provides
    internal fun provideLastPlayedPodcastArtistsDao(db: AppDatabase): RecentlyPlayedPodcastArtistDao {
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
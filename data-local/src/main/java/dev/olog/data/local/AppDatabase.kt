package dev.olog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.olog.data.local.equalizer.preset.EqualizerPresetEntity
import dev.olog.data.local.equalizer.preset.EqualizerPresetsDao
import dev.olog.data.local.favorite.FavoriteDao
import dev.olog.data.local.favorite.FavoriteEntity
import dev.olog.data.local.favorite.FavoritePodcastEntity
import dev.olog.data.local.history.HistoryDao
import dev.olog.data.local.history.HistoryEntity
import dev.olog.data.local.history.PodcastHistoryEntity
import dev.olog.data.local.last.fm.LastFmAlbumEntity
import dev.olog.data.local.last.fm.LastFmArtistEntity
import dev.olog.data.local.last.fm.LastFmDao
import dev.olog.data.local.last.fm.LastFmTrackEntity
import dev.olog.data.local.lyrics.OfflineLyricsDao
import dev.olog.data.local.lyrics.OfflineLyricsEntity
import dev.olog.data.local.lyrics.sync.LyricsSyncAdjustmentDao
import dev.olog.data.local.lyrics.sync.LyricsSyncAdjustmentEntity
import dev.olog.data.local.most.played.*
import dev.olog.data.local.playing.queue.PlayingQueueDao
import dev.olog.data.local.playing.queue.PlayingQueueEntity
import dev.olog.data.local.playlist.*
import dev.olog.data.local.podcast.PodcastPositionDao
import dev.olog.data.local.podcast.PodcastPositionEntity
import dev.olog.data.local.recently.played.*
import dev.olog.data.local.search.RecentSearchesDao
import dev.olog.data.local.search.RecentSearchesEntity


@Database(
    entities = arrayOf(
        PlayingQueueEntity::class,
        FolderMostPlayedEntity::class,
        PlaylistMostPlayedEntity::class,
        GenreMostPlayedEntity::class,

        FavoriteEntity::class,
        FavoritePodcastEntity::class,

        RecentSearchesEntity::class,

        HistoryEntity::class,
        PodcastHistoryEntity::class,

        RecentlyPlayedAlbumEntity::class,
        RecentlyPlayedArtistEntity::class,
        RecentlyPlayedPodcastAlbumEntity::class,
        RecentlyPlayedPodcastArtistEntity::class,

        LastFmTrackEntity::class,
        LastFmAlbumEntity::class,
        LastFmArtistEntity::class,

        OfflineLyricsEntity::class,

        PlaylistEntity::class,
        PlaylistTrackEntity::class,

        PodcastPlaylistEntity::class,
        PodcastPlaylistTrackEntity::class,

        PodcastPositionEntity::class,

        LyricsSyncAdjustmentEntity::class,
        EqualizerPresetEntity::class

    ), version = 18, exportSchema = true
)
@TypeConverters(CustomTypeConverters::class)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun playingQueueDao(): PlayingQueueDao

    abstract fun folderMostPlayedDao(): FolderMostPlayedDao

    abstract fun playlistMostPlayedDao(): PlaylistMostPlayedDao

    abstract fun genreMostPlayedDao(): GenreMostPlayedDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun recentSearchesDao(): RecentSearchesDao

    abstract fun historyDao(): HistoryDao

    abstract fun lastPlayedAlbumDao(): RecentlyPlayedAlbumDao
    abstract fun lastPlayedArtistDao(): RecentlyPlayedArtistDao
    abstract fun lastPlayedPodcastArtistDao(): RecentlyPlayedPodcastArtistDao
    abstract fun lastPlayedPodcastAlbumDao(): RecentlyPlayedPodcastAlbumDao

    abstract fun lastFmDao(): LastFmDao

    abstract fun offlineLyricsDao(): OfflineLyricsDao

    abstract fun playlistDao(): PlaylistDao
    abstract fun podcastPlaylistDao(): PodcastPlaylistDao

    abstract fun podcastPositionDao(): PodcastPositionDao

    abstract fun lyricsSyncAdjustmentDao(): LyricsSyncAdjustmentDao
    abstract fun equalizerPresetsDao(): EqualizerPresetsDao
}
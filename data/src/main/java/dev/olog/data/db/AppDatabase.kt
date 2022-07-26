package dev.olog.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.olog.data.blacklist.db.BlacklistDao
import dev.olog.data.blacklist.db.BlacklistEntity
import dev.olog.data.db.equalizer.EqualizerPresetEntity
import dev.olog.data.db.equalizer.EqualizerPresetsDao
import dev.olog.data.db.favorite.FavoriteDao
import dev.olog.data.db.favorite.FavoriteEntity
import dev.olog.data.db.favorite.FavoritePodcastEntity
import dev.olog.data.db.history.HistoryDao
import dev.olog.data.db.history.HistoryEntity
import dev.olog.data.db.history.PodcastHistoryEntity
import dev.olog.data.db.last.played.LastPlayedAlbumDao
import dev.olog.data.db.last.played.LastPlayedAlbumEntity
import dev.olog.data.db.last.played.LastPlayedArtistDao
import dev.olog.data.db.last.played.LastPlayedArtistEntity
import dev.olog.data.db.last.played.LastPlayedPodcastAlbumDao
import dev.olog.data.db.last.played.LastPlayedPodcastAlbumEntity
import dev.olog.data.db.last.played.LastPlayedPodcastArtistDao
import dev.olog.data.db.last.played.LastPlayedPodcastArtistEntity
import dev.olog.data.db.lastfm.LastFmAlbumEntity
import dev.olog.data.db.lastfm.LastFmArtistEntity
import dev.olog.data.db.lastfm.LastFmDao
import dev.olog.data.db.lastfm.LastFmTrackEntity
import dev.olog.data.db.lyrics.LyricsSyncAdjustmentDao
import dev.olog.data.db.lyrics.LyricsSyncAdjustmentEntity
import dev.olog.data.db.lyrics.OfflineLyricsDao
import dev.olog.data.db.lyrics.OfflineLyricsEntity
import dev.olog.data.mediastore.MediaStoreAudioDao
import dev.olog.data.mediastore.MediaStoreAudioEntity
import dev.olog.data.mediastore.MediaStoreSongView
import dev.olog.data.mediastore.MediaStoreSortedSongView
import dev.olog.data.db.most.played.FolderMostPlayedDao
import dev.olog.data.db.most.played.FolderMostPlayedEntity
import dev.olog.data.db.most.played.GenreMostPlayedDao
import dev.olog.data.db.most.played.GenreMostPlayedEntity
import dev.olog.data.db.most.played.PlaylistMostPlayedDao
import dev.olog.data.db.most.played.PlaylistMostPlayedEntity
import dev.olog.data.db.playlist.PlaylistDao
import dev.olog.data.db.playlist.PlaylistEntity
import dev.olog.data.db.playlist.PlaylistTrackEntity
import dev.olog.data.db.playlist.PodcastPlaylistDao
import dev.olog.data.db.playlist.PodcastPlaylistEntity
import dev.olog.data.db.playlist.PodcastPlaylistTrackEntity
import dev.olog.data.db.podcast.PodcastPositionDao
import dev.olog.data.db.podcast.PodcastPositionEntity
import dev.olog.data.db.queue.PlayingQueueDao
import dev.olog.data.db.queue.PlayingQueueEntity
import dev.olog.data.db.recent.search.RecentSearchesDao
import dev.olog.data.db.recent.search.RecentSearchesEntity
import dev.olog.data.mediastore.MediaStoreSongViewDao
import dev.olog.data.song.SongDao
import dev.olog.data.sort.db.SortDao
import dev.olog.data.sort.db.SortEntity
import dev.olog.data.sort.db.SortTypeConverters


@Database(
    entities = arrayOf(
        MediaStoreAudioEntity::class,
        BlacklistEntity::class,
        SortEntity::class,

        PlayingQueueEntity::class,
        FolderMostPlayedEntity::class,
        PlaylistMostPlayedEntity::class,
        GenreMostPlayedEntity::class,

        FavoriteEntity::class,
        FavoritePodcastEntity::class,

        RecentSearchesEntity::class,

        HistoryEntity::class,
        PodcastHistoryEntity::class,

        LastPlayedAlbumEntity::class,
        LastPlayedArtistEntity::class,
        LastPlayedPodcastAlbumEntity::class,
        LastPlayedPodcastArtistEntity::class,

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

    ),
    views = [
        MediaStoreSongView::class,
        MediaStoreSortedSongView::class,
    ],
    version = 19,
    exportSchema = true
)
@TypeConverters(
    CustomTypeConverters::class,
    SortTypeConverters::class,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mediaStoreAudioDao(): MediaStoreAudioDao
    abstract fun mediaStoreSongDao(): MediaStoreSongViewDao

    abstract fun blacklistDao(): BlacklistDao
    abstract fun sortDao(): SortDao

    abstract fun songDao(): SongDao

    abstract fun playingQueueDao(): PlayingQueueDao

    abstract fun folderMostPlayedDao(): FolderMostPlayedDao

    abstract fun playlistMostPlayedDao(): PlaylistMostPlayedDao

    abstract fun genreMostPlayedDao(): GenreMostPlayedDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun recentSearchesDao(): RecentSearchesDao

    abstract fun historyDao(): HistoryDao

    abstract fun lastPlayedAlbumDao(): LastPlayedAlbumDao
    abstract fun lastPlayedArtistDao(): LastPlayedArtistDao
    abstract fun lastPlayedPodcastArtistDao(): LastPlayedPodcastArtistDao
    abstract fun lastPlayedPodcastAlbumDao(): LastPlayedPodcastAlbumDao

    abstract fun lastFmDao(): LastFmDao

    abstract fun offlineLyricsDao(): OfflineLyricsDao

    abstract fun playlistDao(): PlaylistDao
    abstract fun podcastPlaylistDao(): PodcastPlaylistDao

    abstract fun podcastPositionDao(): PodcastPositionDao

    abstract fun lyricsSyncAdjustmentDao(): LyricsSyncAdjustmentDao
    abstract fun equalizerPresetsDao(): EqualizerPresetsDao
}
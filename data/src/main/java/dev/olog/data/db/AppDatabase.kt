package dev.olog.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.olog.data.blacklist.BlacklistDao
import dev.olog.data.blacklist.BlacklistEntity
import dev.olog.data.db.dao.EqualizerPresetsDao
import dev.olog.data.db.dao.FavoriteDao
import dev.olog.data.db.dao.FolderMostPlayedDao
import dev.olog.data.db.dao.GenreMostPlayedDao
import dev.olog.data.db.dao.HistoryDao
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.db.dao.LastPlayedAlbumDao
import dev.olog.data.db.dao.LastPlayedArtistDao
import dev.olog.data.db.dao.LastPlayedPodcastAlbumDao
import dev.olog.data.db.dao.LastPlayedPodcastArtistDao
import dev.olog.data.db.dao.LyricsSyncAdjustmentDao
import dev.olog.data.db.dao.OfflineLyricsDao
import dev.olog.data.db.dao.PlayingQueueDao
import dev.olog.data.db.dao.PlaylistMostPlayedDao
import dev.olog.data.db.dao.PodcastPositionDao
import dev.olog.data.db.dao.RecentSearchesDao
import dev.olog.data.db.entities.*
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.MediaStoreAudioInternalDao
import dev.olog.data.mediastore.MediaStoreAudioInternalEntity
import dev.olog.data.mediastore.MediaStorePlaylistInternalEntity
import dev.olog.data.mediastore.MediaStorePlaylistMembersInternalEntity
import dev.olog.data.mediastore.album.MediaStoreAlbumDao
import dev.olog.data.mediastore.album.MediaStoreAlbumEntity
import dev.olog.data.mediastore.artist.MediaStoreArtistDao
import dev.olog.data.mediastore.audio.MediaStoreAudioDao
import dev.olog.data.mediastore.folder.MediaStoreFolderEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.mediastore.folder.MediaStoreFolderDao
import dev.olog.data.mediastore.genre.MediaStoreGenreDao
import dev.olog.data.mediastore.genre.MediaStoreGenreEntity
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDao
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDirectoryEntity
import dev.olog.data.mediastore.playlist.MediaStorePlaylistEntity


@Database(
    entities = arrayOf(
        MediaStoreAudioInternalEntity::class,
        MediaStorePlaylistInternalEntity::class,
        MediaStorePlaylistMembersInternalEntity::class,
        MediaStorePlaylistDirectoryEntity::class,
        BlacklistEntity::class,

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

        PodcastPositionEntity::class,

        LyricsSyncAdjustmentEntity::class,
        EqualizerPresetEntity::class

    ),
    views = [
        MediaStoreAudioEntity::class,
        MediaStoreFolderEntity::class,
        MediaStoreArtistEntity::class,
        MediaStoreAlbumEntity::class,
        MediaStoreGenreEntity::class,
        MediaStorePlaylistEntity::class,
    ],
    version = 19,
    exportSchema = true,
)
@TypeConverters(CustomTypeConverters::class)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun mediaStoreAudioInternalDao(): MediaStoreAudioInternalDao
    abstract fun mediaStoreAudioDao(): MediaStoreAudioDao
    abstract fun mediaStoreFolderDao(): MediaStoreFolderDao
    abstract fun mediaStoreArtistDao(): MediaStoreArtistDao
    abstract fun mediaStoreAlbumDao(): MediaStoreAlbumDao
    abstract fun mediaStoreGenreDao(): MediaStoreGenreDao
    abstract fun mediaStorePlaylistDao(): MediaStorePlaylistDao
    abstract fun blacklistDao(): BlacklistDao

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

    abstract fun podcastPositionDao(): PodcastPositionDao

    abstract fun lyricsSyncAdjustmentDao(): LyricsSyncAdjustmentDao
    abstract fun equalizerPresetsDao(): EqualizerPresetsDao
}
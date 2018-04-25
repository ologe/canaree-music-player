package dev.olog.msc.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import dev.olog.msc.data.entity.*


@Database(entities = arrayOf(
        PlayingQueueEntity::class,
        FolderMostPlayedEntity::class,
        PlaylistMostPlayedEntity::class,
        GenreMostPlayedEntity::class,
        FavoriteEntity::class,
        RecentSearchesEntity::class,
        HistoryEntity::class,
        LastPlayedAlbumEntity::class,
        LastPlayedArtistEntity::class,

        LastFmTrackEntity::class,
        LastFmAlbumEntity::class,
        LastFmArtistEntity::class,

        OfflineLyricsEntity::class

), version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playingQueueDao(): PlayingQueueDao

    abstract fun folderMostPlayedDao(): FolderMostPlayedDao

    abstract fun playlistMostPlayedDao(): PlaylistMostPlayedDao

    abstract fun genreMostPlayedDao(): GenreMostPlayedDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun recentSearchesDao(): RecentSearchesDao

    abstract fun historyDao(): HistoryDao

    abstract fun lastPlayedAlbumDao() : LastPlayedAlbumDao
    abstract fun lastPlayedArtistDao() : LastPlayedArtistDao

    abstract fun lastFmDao() : LastFmDao

    abstract fun offlineLyricsDao(): OfflineLyricsDao

}
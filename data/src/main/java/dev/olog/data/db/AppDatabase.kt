package dev.olog.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import dev.olog.data.entity.*

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

        ImageFolderEntity::class,
        ImagePlaylistEntity::class,
        ImageArtistEntity::class,
        ImageGenreEntity::class

), version = 1, exportSchema = false)
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

    abstract fun folderImagesDao(): FolderImagesDao

}
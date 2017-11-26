package dev.olog.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import dev.olog.data.entity.FolderMostPlayedEntity
import dev.olog.data.entity.GenreMostPlayedEntity
import dev.olog.data.entity.PlayingQueueEntity
import dev.olog.data.entity.PlaylistMostPlayedEntity

@Database(entities = arrayOf(
        PlayingQueueEntity::class,
        FolderMostPlayedEntity::class,
        PlaylistMostPlayedEntity::class,
        GenreMostPlayedEntity::class

), version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun playingQueueDao(): PlayingQueueDao

    abstract fun folderMostPlayedDao(): FolderMostPlayedDao

    abstract fun playlistMostPlayedDao(): PlaylistMostPlayedDao

    abstract fun genreMostPlayedDao(): GenreMostPlayedDao

}
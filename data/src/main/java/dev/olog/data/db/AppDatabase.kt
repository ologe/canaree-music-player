package dev.olog.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import dev.olog.data.model.PlayingQueueEntity

@Database(entities = arrayOf(
        PlayingQueueEntity::class

), version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun playingQueueDao(): PlayingQueueDao

}
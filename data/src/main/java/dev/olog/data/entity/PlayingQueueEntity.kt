package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "playing_queue",
        indices = arrayOf(Index("id")))
data class PlayingQueueEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val value: Long,
        val timeAdded: Long = System.currentTimeMillis()
)

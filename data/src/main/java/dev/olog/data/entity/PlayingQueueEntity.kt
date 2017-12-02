package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "playing_queue",
        indices = arrayOf(Index("index")))
data class PlayingQueueEntity(
        @PrimaryKey(autoGenerate = true)
        val index: Int = 0,
        val value: Long
)

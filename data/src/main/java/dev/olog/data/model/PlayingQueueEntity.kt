package dev.olog.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "playing_queue",
        indices = arrayOf(Index("value")))
data class PlayingQueueEntity(
        @PrimaryKey var value: Long
)

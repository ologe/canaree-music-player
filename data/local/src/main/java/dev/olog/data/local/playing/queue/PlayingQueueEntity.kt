package dev.olog.data.local.playing.queue

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playing_queue",
    indices = [(Index("internalId"))]
)
data class PlayingQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val internalId: Int = 0,
    val songId: Long,
    val serviceProgressive: Int
)
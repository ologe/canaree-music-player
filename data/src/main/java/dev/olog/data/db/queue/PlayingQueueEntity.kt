package dev.olog.data.db.queue

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playing_queue",
    indices = [(Index("progressive"))]
)
data class PlayingQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val progressive: Int = 0,
    val category: String,
    val categoryValue: String,
    val songId: Long,
    val idInPlaylist: Int
)
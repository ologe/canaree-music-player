package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "playing_queue",
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

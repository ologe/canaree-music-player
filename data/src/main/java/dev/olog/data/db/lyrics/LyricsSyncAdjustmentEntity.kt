package dev.olog.data.db.lyrics

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lyrics_sync_adjustment",
    indices = [(Index("id"))]
)
data class LyricsSyncAdjustmentEntity(
    @PrimaryKey
    val id: Long,
    val millis: Long
)
package dev.olog.msc.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "song_history",
        indices = [(Index("id"))]
)
data class HistoryEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val songId: Long,
        var dateAdded : Long = System.currentTimeMillis()
)

package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "song_history",
        indices = arrayOf(Index("id"))
)
data class HistoryEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val songId: Long,
        var dateAdded : Long = System.currentTimeMillis()
)

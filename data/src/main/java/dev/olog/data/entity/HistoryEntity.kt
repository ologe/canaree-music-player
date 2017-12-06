package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "song_history",
        indices = arrayOf(Index("songId"))
)
data class HistoryEntity(
        @PrimaryKey var songId: Long,
        var dateAdded : Long
) {

    companion object {
        fun from(songId: Long) = HistoryEntity(
                songId = songId,
                dateAdded = System.currentTimeMillis()
        )
    }

}

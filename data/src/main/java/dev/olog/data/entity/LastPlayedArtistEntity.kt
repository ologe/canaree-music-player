package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName ="last_played_artists",
        indices = arrayOf(Index("id"))
)
data class LastPlayedArtistEntity(
        @PrimaryKey var id: Long,
        var name: String,
        var dateAdded: Long = System.currentTimeMillis()
)
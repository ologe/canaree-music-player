package dev.olog.data.local.recently.played

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_played_artists",
    indices = [(Index("id"))]
)
data class RecentlyPlayedArtistEntity(
    @PrimaryKey
    val id: Long,
    val dateAdded: Long = System.currentTimeMillis()
)
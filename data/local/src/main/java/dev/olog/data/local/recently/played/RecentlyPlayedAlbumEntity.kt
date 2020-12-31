package dev.olog.data.local.recently.played

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_played_albums",
    indices = [(Index("id"))]
)
data class RecentlyPlayedAlbumEntity(
    @PrimaryKey
    val id: Long,
    val dateAdded: Long = System.currentTimeMillis()
)
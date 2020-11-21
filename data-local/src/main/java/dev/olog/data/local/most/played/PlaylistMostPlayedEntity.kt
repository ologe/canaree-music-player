package dev.olog.data.local.most.played

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "most_played_playlist",
    indices = [(Index("id"))]
)
data class PlaylistMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val songId: Long,
    val playlistId: Long
)
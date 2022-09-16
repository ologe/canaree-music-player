package dev.olog.data.song.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
@Deprecated("migrate to new playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val size: Int
)
package dev.olog.data.db.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_playlist")
data class PodcastPlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val size: Int
)
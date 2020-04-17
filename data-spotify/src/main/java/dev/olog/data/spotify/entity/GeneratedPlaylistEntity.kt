package dev.olog.data.spotify.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "generated_playlist", indices = [Index("playlistId")])
data class GeneratedPlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val title: String,
    val tracks: List<Long>
)
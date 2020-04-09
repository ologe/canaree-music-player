package dev.olog.data.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "offline_lyrics",
    indices = [(Index("trackId"))]
)
data class OfflineLyricsEntity(
    @PrimaryKey
    val trackId: Long,
    val lyrics: String
)
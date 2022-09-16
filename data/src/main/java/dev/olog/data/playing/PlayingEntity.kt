package dev.olog.data.playing

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playing")
data class PlayingEntity(
    @PrimaryKey
    val id: String,
)
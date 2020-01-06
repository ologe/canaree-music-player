package dev.olog.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_position")
data class PodcastPositionEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val position: Long
)
package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "podcast_position")
data class PodcastPositionEntity(
        @PrimaryKey(autoGenerate = false) val id: Long,
        val position: Long
)
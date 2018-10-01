package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "podcast_song_history",
        indices = [(Index("id"))]
)
data class PodcastHistoryEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val podcastId: Long,
        var dateAdded : Long = System.currentTimeMillis()
)

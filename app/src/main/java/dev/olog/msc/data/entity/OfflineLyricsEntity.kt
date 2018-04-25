package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "offline_lyrics",
        indices = [(Index("trackId"))]
)
data class OfflineLyricsEntity(
        @PrimaryKey var trackId: Long,
        var lyrics: String
)
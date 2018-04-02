package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName ="last_played_artists",
        indices = [(Index("id"))]
)
data class LastPlayedArtistEntity(
        @PrimaryKey var id: Long,
        var dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_played_albums",
        indices = [(Index("id"))]
)
data class LastPlayedAlbumEntity(
        @PrimaryKey var id: Long,
        var dateAdded: Long = System.currentTimeMillis()
)
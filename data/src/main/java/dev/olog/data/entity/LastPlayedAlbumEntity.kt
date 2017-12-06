package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "last_played_albums",
        indices = arrayOf(Index("id")))
data class LastPlayedAlbumEntity(
        @PrimaryKey var id: Long,
        var artistId: Long,
        var title: String,
        var artist: String,
        var image: String,
        var dateAdded: Long = System.currentTimeMillis()
)
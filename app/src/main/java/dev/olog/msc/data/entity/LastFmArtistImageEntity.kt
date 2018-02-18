package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "last_fm_artist_image",
        indices = arrayOf(Index("id")))
data class LastFmArtistImageEntity(
        @PrimaryKey val id: Long,
        val image: String
)
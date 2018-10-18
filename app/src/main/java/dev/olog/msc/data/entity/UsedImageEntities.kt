package dev.olog.msc.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "used_image_track")
data class UsedTrackImageEntity(
        @PrimaryKey val id: Long,
        val image: String
)

@Entity(tableName = "used_image_album")
data class UsedAlbumImageEntity(
        @PrimaryKey val id: Long,
        val image: String
)

@Entity(tableName = "used_image_artist")
data class UsedArtistImageEntity(
        @PrimaryKey val id: Long,
        val image: String
)
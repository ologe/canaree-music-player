package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "used_image_track")
class UsedTrackImageEntity(
    @PrimaryKey
    @JvmField
    val id: Long,
    @JvmField
    val image: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedTrackImageEntity

        if (id != other.id) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + image.hashCode()
        return result
    }
}

@Entity(tableName = "used_image_album")
class UsedAlbumImageEntity(
    @PrimaryKey
    @JvmField
    val id: Long,
    @JvmField
    val image: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedAlbumImageEntity

        if (id != other.id) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + image.hashCode()
        return result
    }
}

@Entity(tableName = "used_image_artist")
class UsedArtistImageEntity(
    @PrimaryKey
    @JvmField
    val id: Long,
    @JvmField
    val image: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedArtistImageEntity

        if (id != other.id) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + image.hashCode()
        return result
    }
}
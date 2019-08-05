package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_version",
    indices = [(Index("mediaId"))]
)
class ImageVersionEntity(
    @PrimaryKey
    @JvmField
    val mediaId: String,
    @JvmField
    val version: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageVersionEntity

        if (mediaId != other.mediaId) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + version
        return result
    }
}
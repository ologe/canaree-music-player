package dev.olog.core.entity

class UsedTrackImage(
    @JvmField
    val id: Long,
    @JvmField
    val image: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedTrackImage

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

class UsedAlbumImage(
    @JvmField
    val id: Long,
    @JvmField
    val image: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedAlbumImage

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

class UsedArtistImage(
    @JvmField
    val id: Long,
    @JvmField
    val image: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedArtistImage

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
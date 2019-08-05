package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

class Genre(
    @JvmField
    val id: Long,
    @JvmField
    val name: String,
    @JvmField
    val size: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Genre

        if (id != other.id) return false
        if (name != other.name) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + size
        return result
    }

    fun getMediaId(): MediaId {
        return MediaId.createCategoryValue(MediaIdCategory.GENRES, id.toString())
    }

    fun withSongs(songs: Int): Genre {
        return Genre(
            id = id,
            name = name,
            size = songs
        )
    }

}
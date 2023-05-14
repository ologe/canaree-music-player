package dev.olog.core.entity.track

import dev.olog.core.MediaId

data class Genre(
    val id: Long,
    val name: String,
    val size: Int
) {

    fun getMediaId(): MediaId {
        return MediaId.ofGenre(id, false)
    }

}
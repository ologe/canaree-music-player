package dev.olog.core.entity.track

import dev.olog.core.MediaId

data class Artist(
    val id: Long,
    val name: String,
    val albumArtist: String,
    val songs: Int,
    val isPodcast: Boolean
) {

    fun getMediaId(): MediaId {
        return MediaId.ofArtist(id, isPodcast)
    }

}
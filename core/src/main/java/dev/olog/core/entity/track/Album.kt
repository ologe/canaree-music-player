package dev.olog.core.entity.track

import dev.olog.core.MediaId

data class Album(
    val id: Long,
    val artistId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val size: Int,
    val isPodcast: Boolean
) {


    fun getMediaId(): MediaId {
        return MediaId.ofAlbum(id, isPodcast)
    }

    fun getArtistMediaId(): MediaId {
        return MediaId.ofArtist(artistId, isPodcast)
    }

}
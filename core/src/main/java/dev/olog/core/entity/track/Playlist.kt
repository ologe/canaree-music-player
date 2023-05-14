package dev.olog.core.entity.track

import dev.olog.core.MediaId

// TODO maybe split it in a sealed class/interface? playlist and autoplaylist
data class Playlist(
    val id: Long,
    val title: String,
    val size: Int,
    val isPodcast: Boolean,
    val path: String?,
) {

    fun getMediaId(): MediaId {
        return MediaId.ofPlaylist(id, isPodcast)
    }

}
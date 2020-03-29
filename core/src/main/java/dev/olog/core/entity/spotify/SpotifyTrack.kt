package dev.olog.core.entity.spotify

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

data class SpotifyTrack(
    val id: String,
    val name: String,
    val uri: String,
    val image: String,
    val discNumber: Int,
    val trackNumber: Int,
    val duration: Long,
    val isExplicit: Boolean
) {

    val mediaId: MediaId.Track
        get() = MediaId.Track(MediaIdCategory.SPOTIFY_TRACK, uri, -1)

}
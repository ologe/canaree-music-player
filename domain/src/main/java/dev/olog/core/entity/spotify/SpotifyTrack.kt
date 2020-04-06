package dev.olog.core.entity.spotify

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

data class SpotifyTrack(
    val id: String,
    val name: String,
    val artist: String,
    val album: String,
    val uri: String,
    val image: String,
    val discNumber: Int,
    val trackNumber: Int,
    val duration: Long,
    val isExplicit: Boolean,
    val previewUrl: String?
) {

    companion object {
        const val INVALID_PREVIEW_URL = "invalid"
    }

    val mediaId: MediaId.Track
        get() = MediaId.Track(MediaIdCategory.SPOTIFY_TRACK, uri, previewUrl ?: INVALID_PREVIEW_URL)

}
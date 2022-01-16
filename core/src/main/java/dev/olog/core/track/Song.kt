package dev.olog.core.track

import dev.olog.core.MediaUri

data class Song(
    val uri: MediaUri,
    val artistUri: MediaUri,
    val albumUri: MediaUri,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val directory: String,
    val path: String,
    val discNumber: Int,
    val trackNumber: Int,
    val idInPlaylist: Int, // TODO remove
) {

    val isPodcast: Boolean
        get() = uri.isPodcast

    companion object

}
package dev.olog.core.entity

import dev.olog.core.MediaId

data class PlayingQueueSong(
    val id: Long,
    val idInPlaylist: Int,
    val parentMediaId: MediaId,
    val artistId: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val path: String,
    val folder: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean
)

fun PlayingQueueSong.getMediaId(): MediaId {
    return MediaId.playableItem(parentMediaId, id)
}
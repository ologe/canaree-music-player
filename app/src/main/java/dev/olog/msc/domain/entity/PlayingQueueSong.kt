package dev.olog.msc.domain.entity

import dev.olog.core.MediaId
import java.io.File

data class PlayingQueueSong (
    val id: Long,
    val idInPlaylist: Int,
    val parentMediaId: MediaId,
    val artistId: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val image: String,
    val duration: Long,
    val dateAdded: Long,
    val path: String,
    val folder: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean) {

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

}
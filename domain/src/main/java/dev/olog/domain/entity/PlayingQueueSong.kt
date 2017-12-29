package dev.olog.domain.entity

import dev.olog.shared.MediaId
import java.io.File

data class PlayingQueueSong (
        val id: Long,
        val parentMediaId: MediaId,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val album: String,
        val image: String,
        val duration: Long,
        val dateAdded: Long,
        val isRemix: Boolean,
        val isExplicit: Boolean,
        val path: String,
        val folder: String,
        val trackNumber: Int) {

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

}
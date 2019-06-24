package dev.olog.core.entity

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
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

fun PlayingQueueSong.getMediaId(): MediaId {
    val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
    val mediaId = MediaId.createCategoryValue(category, "")
    return MediaId.playableItem(mediaId, id)
}
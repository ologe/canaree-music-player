package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Track
import dev.olog.core.MediaIdCategory.*
import java.io.File

data class Song(
    val id: Long,
    val artistId: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val path: String,
    val trackColumn: Int,
    val idInPlaylist: Int,
    val isPodcast: Boolean,
    val displayName: String

) {

    val discNumber: Int
        get() {
            if (trackColumn >= 1000) {
                return trackColumn / 1000
            }
            return 0
        }

    val trackNumber: Int
        get() {
            if (trackColumn >= 1000) {
                return trackColumn % 1000
            }
            return trackColumn
        }

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

    val mediaId: Track
        get() {
            val category = if (isPodcast) MediaId.PODCAST_CATEGORY else MediaId.SONGS_CATEGORY
            return category.playableItem(id)
        }

    val parentMediaId: Category
        get() = mediaId.parentId

    val albumMediaId: Category
        get() {
            val category = ALBUMS
            return Category(category, albumId)
        }

    val artistMediaId: Category
        get() {
            val category = if (isPodcast) PODCASTS_AUTHORS else ARTISTS
            return Category(category, albumId)
        }

}
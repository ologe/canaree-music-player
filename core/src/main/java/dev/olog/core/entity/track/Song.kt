package dev.olog.core.entity.track

import android.content.ContentValues
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
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

    fun toContentValues(): ContentValues {
        return ContentValues().apply {
            put("_id", id)
            put("artist_id", artistId)
            put("album_id", albumId)
            put("title", title)
            put("artist", artist)
            put("album", album)
            put("album_artist", albumArtist)
            put("duration", duration)
            put("date_added", dateAdded)
            put("date_modified", dateModified)
            put("_data", path)
            put("track", trackColumn)
            put("is_podcast", if (isPodcast) 1 else 0)
            put("_display_name", displayName)
        }
    }

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

    fun getMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.createCategoryValue(category, "")
        return MediaId.playableItem(mediaId, id)
    }

    fun getAlbumMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS_ALBUMS else MediaIdCategory.ALBUMS
        return MediaId.createCategoryValue(category, this.albumId.toString())
    }

    fun getArtistMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS_ARTISTS else MediaIdCategory.ARTISTS
        return MediaId.createCategoryValue(category, this.artistId.toString())
    }

}
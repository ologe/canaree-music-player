package dev.olog.core.entity.podcast

import dev.olog.core.entity.track.Song
import java.io.File

data class Podcast(
    val id: Long,
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
    val trackColumn: Int,
    val idInPlaylist: Int
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

    val hasAlbumNameAsFolder: Boolean
        get() = album == folder

}

fun Podcast.toSong(): Song {
    return Song(
        this.id,
        this.artistId,
        this.albumId,
        this.title,
        this.artist,
        this.albumArtist,
        this.album,
        this.duration,
        this.dateAdded,
        this.path,
        this.folder,
        this.discNumber, // TODO match song
        this.idInPlaylist
    )
}
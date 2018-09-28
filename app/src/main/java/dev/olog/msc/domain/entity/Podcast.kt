package dev.olog.msc.domain.entity

import java.io.File

data class Podcast (
        val id: Long,
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
        val trackNumber: Int) {

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

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
            this.image,
            this.duration,
            this.dateAdded,
            this.path,
            this.folder,
            this.discNumber,
            this.trackNumber
    )
}
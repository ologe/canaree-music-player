package dev.olog.core.entity

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
    val path: String,
    val folder: String,
    val trackColumn: Int,
    val idInPlaylist: Int

) {


    val discNumber: Int
        get() {
            if (trackColumn >= 1000){
                return trackColumn / 1000
            }
            return 0
        }
    val trackNumber: Int
        get() {
            if (trackColumn >= 1000){
                return trackColumn % 1000
            }
            return trackColumn
        }

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

    val hasAlbumNameAsFolder: Boolean
        get() = album == folderPath

}
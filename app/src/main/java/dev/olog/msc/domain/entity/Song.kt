package dev.olog.msc.domain.entity

import java.io.File

data class Song (
        val id: Long,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val album: String,
        val albumArtist: String,
        val image: String,
        val duration: Long,
        val dateAdded: Long,
        val isRemix: Boolean,
        val isExplicit: Boolean,
        val path: String,
        val folder: String,
        val discNumber: Int,
        val trackNumber: Int) {

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

}
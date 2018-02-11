package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.constants.Constants
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.getInt
import dev.olog.msc.utils.k.extension.getLong
import dev.olog.msc.utils.k.extension.getString
import java.io.File


fun Cursor.toSong(context: Context): Song {

    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getString(MediaStore.MediaColumns.DATA)
    val folder = extractFolder(path)

    val (title, isExplicit, isRemix) = adjustTitle(getString(MediaStore.MediaColumns.TITLE))

    val artist = adjustArtist(getString(MediaStore.Audio.AudioColumns.ARTIST))
    val album = adjustAlbum(getString(MediaStore.Audio.AudioColumns.ALBUM), folder)

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)

    return Song(
            id, artistId, albumId, title, artist, album,
            ImagesFolderUtils.forAlbum(context, albumId),
            duration, dateAdded, isRemix, isExplicit, path, folder,
            trackNumber)
}


private fun extractFolder(path: String): String {
    val lastSep = path.lastIndexOf(File.separator)
    val prevSep = path.lastIndexOf(File.separator, lastSep - 1)
    return path.substring(prevSep + 1, lastSep)
}

private fun adjustAlbum(album: String, folder: String): String {
    val hasUnknownAlbum = album == Constants.UNKNOWN || album == folder
    return if (hasUnknownAlbum) {
        Constants.UNKNOWN_ALBUM
    } else {
        album
    }
}

private fun adjustArtist(artist: String): String {
    val hasUnknownArtist = artist == Constants.UNKNOWN
    return if (hasUnknownArtist) {
        Constants.UNKNOWN_ARTIST
    } else {
        artist
    }
}

private fun adjustTitle(title: String): Triple<String, Boolean, Boolean> {
    val builder = StringBuilder(title)

    var isExplicit = false
    var isRemix = false

    var startRound = builder.indexOf("(")
    var startSquare = builder.indexOf("[")
    var start: Int
    if (startRound > -1 && startSquare > -1) {
        start = Math.min(startRound, startSquare)
    } else if (startRound > -1) {
        start = startRound
    } else {
        start = startSquare
    }

    var endRound: Int
    var endSquare: Int
    var end: Int

    while (start > 0) {
        endRound = builder.indexOf(")", start) + 1
        endSquare = builder.indexOf("]", start) + 1
        if (endRound > start && endSquare > start) {
            end = Math.min(endRound, endSquare)
        } else if (endRound > start) {
            end = endRound
        } else {
            end = endSquare
        }

        if (end > start) {

            val substring = builder.toString().toLowerCase().substring(start, end)

            val canDelete = substring.contains("official") || substring.contains("lyrics") ||
                    substring.contains("audio") || substring.contains("video") || substring.contains("hd")

            if (canDelete) {
                builder.replace(start, end, "")

            } else if (substring.contains("explicit")) {
                builder.replace(start, end, "")
                isExplicit = true
            } else if (substring.contains("remix")) {
                builder.replace(start, end, "")
                isRemix = true
            } else {
                start = end
            }

            startRound = builder.indexOf("(", start)
            startSquare = builder.indexOf("[", start)
            if (startRound > start && startSquare > start) {
                start = Math.min(startRound, startSquare)
            } else if (startRound > start) {
                start = startRound
            } else {
                start = startSquare
            }
        }
    }

    return Triple(builder.toString(), isExplicit, isRemix)
}
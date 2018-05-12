package dev.olog.msc.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.core.database.getInt
import androidx.core.database.getLong
import androidx.core.database.getString
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import java.io.File


fun Cursor.toSong(): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getString(MediaStore.MediaColumns.DATA)
    val folder = extractFolder(path)

//    val (title, isExplicit, isRemix) = TrackTitleUtils.adjust(getString(MediaStore.MediaColumns.TITLE))
    val title = getString(MediaStore.MediaColumns.TITLE)

    val artist = getString(MediaStore.Audio.AudioColumns.ARTIST)
    val album = adjustAlbum(getString(MediaStore.Audio.AudioColumns.ALBUM), folder)

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val track = extractTrackNumber(trackNumber)
    val disc = extractDiscNumber(trackNumber)

    return Song(
            id, artistId, albumId, title, artist, album,
            ImagesFolderUtils.forAlbum(albumId),
            duration, dateAdded, path,
            folder.capitalize(), disc, track)
}

fun Cursor.toUneditedSong(image: String): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getString(MediaStore.MediaColumns.DATA)
    val folder = extractFolder(path)

    val title = getString(MediaStore.MediaColumns.TITLE)

    val artist = getString(MediaStore.Audio.AudioColumns.ARTIST)
    val album = adjustAlbum(getString(MediaStore.Audio.AudioColumns.ALBUM), folder)

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val track = extractTrackNumber(trackNumber)
    val disc = extractDiscNumber(trackNumber)

    return Song(
            id, artistId, albumId, title, artist, album,
            image, duration, dateAdded, path,
            folder.capitalize(), disc, track)
}

private fun extractTrackNumber(originalTrackNumber: Int) : Int {
    if (originalTrackNumber >= 1000){
        return originalTrackNumber % 1000
    }
    return originalTrackNumber
}

private fun extractDiscNumber(originalTrackNumber: Int): Int {
    if (originalTrackNumber >= 1000){
        return originalTrackNumber / 1000
    }
    return 0
}

private fun extractFolder(path: String): String {
    val lastSep = path.lastIndexOf(File.separator)
    val prevSep = path.lastIndexOf(File.separator, lastSep - 1)
    return path.substring(prevSep + 1, lastSep)
}

private fun adjustAlbum(album: String, folder: String): String {
    val hasUnknownAlbum = album == folder
    return if (hasUnknownAlbum) {
        AppConstants.UNKNOWN
    } else {
        album
    }
}


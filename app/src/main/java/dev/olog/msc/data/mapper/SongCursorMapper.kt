package dev.olog.msc.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.core.database.getInt
import androidx.core.database.getLong
import androidx.core.database.getString
import dev.olog.msc.domain.entity.Song
import java.io.File


fun Cursor.toSong(): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getString(MediaStore.MediaColumns.DATA)
    val folder = extractFolder(path)

    val title = getString(MediaStore.MediaColumns.TITLE)

    val artist = getString(MediaStore.Audio.AudioColumns.ARTIST)
    val album = getString(MediaStore.Audio.AudioColumns.ALBUM)

    var albumArtist = artist
    val albumArtistIndex = this.getColumnIndex("album_artist")
    if (albumArtistIndex != -1) {
        this.getString(albumArtistIndex)?.also {
            albumArtist = it
        }
    }

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val track = extractTrackNumber(trackNumber)
    val disc = extractDiscNumber(trackNumber)

    return Song(
            id, artistId, albumId, title, artist, albumArtist, album,
            "",
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
    val album = getString(MediaStore.Audio.AudioColumns.ALBUM)

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val track = extractTrackNumber(trackNumber)
    val disc = extractDiscNumber(trackNumber)

    var albumArtist = artist
    val albumArtistIndex = this.getColumnIndex("album_artist")
    if (albumArtistIndex != -1) {
        this.getString(albumArtistIndex)?.also {
            albumArtist = it
        }
    }

    return Song(
            id, artistId, albumId, title, artist, albumArtist, album,
            image, duration, dateAdded, path,
            folder.capitalize(), disc, track)
}

internal fun extractTrackNumber(originalTrackNumber: Int) : Int {
    if (originalTrackNumber >= 1000){
        return originalTrackNumber % 1000
    }
    return originalTrackNumber
}

internal fun extractDiscNumber(originalTrackNumber: Int): Int {
    if (originalTrackNumber >= 1000){
        return originalTrackNumber / 1000
    }
    return 0
}

internal fun extractFolder(path: String): String {
    val lastSep = path.lastIndexOf(File.separator)
    val prevSep = path.lastIndexOf(File.separator, lastSep - 1)
    return path.substring(prevSep + 1, lastSep)
}


//internal fun adjustAlbum(album: String, folder: String): String {
//    val hasUnknownAlbum = album == folder
//    return if (hasUnknownAlbum) {
//        AppConstants.UNKNOWN
//    } else {
//        album
//    }
//}

package dev.olog.msc.data.mapper

import android.database.Cursor
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.core.database.getInt
import androidx.core.database.getLong
import androidx.core.database.getString
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Podcast

fun Cursor.toPodcast(): Podcast {
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

    return Podcast(
            id, artistId, albumId, title, artist, albumArtist, album,
            "",
            duration, dateAdded, path,
            folder.capitalize(), disc, track)
}

fun Cursor.toUneditedPodcast(image: String): Podcast {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getString(MediaStore.MediaColumns.DATA)
    val folder = extractFolder(path)

    val title = getString(MediaStore.MediaColumns.TITLE)

    val artist = getString(MediaStore.Audio.AudioColumns.ARTIST)
    val album = adjustAlbum(getString(MediaStore.Audio.AudioColumns.ALBUM))

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

    return Podcast(
            id, artistId, albumId, title, artist, albumArtist, album,
            image, duration, dateAdded, path,
            folder.capitalize(), disc, track)
}

private fun adjustAlbum(album: String): String {
    if (album == Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).name){
        return AppConstants.UNKNOWN_ALBUM
    } else {
        return album
    }
}
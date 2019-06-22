package dev.olog.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.core.entity.Song
import dev.olog.data.queries.Columns
import dev.olog.data.utils.getInt
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getStringOrNull

fun Cursor.toSong(): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getStringOrNull(MediaStore.MediaColumns.DATA) ?: ""

    val title = getStringOrNull(MediaStore.MediaColumns.TITLE) ?: ""

    val artist = getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST) ?: ""
    val album = getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM) ?: ""

    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val track = extractTrackNumber(trackNumber)
    val disc = extractDiscNumber(trackNumber)

    return Song(
        id, artistId, albumId,
        title, artist, albumArtist, album,
        duration, dateAdded, path,
        "", // TODO remove folder
        disc, track
    )
}

// TODO made as function on song
internal fun extractDiscNumber(originalTrackNumber: Int): Int {
    if (originalTrackNumber >= 1000){
        return originalTrackNumber / 1000
    }
    return 0
}

// TODO made as function on song
internal fun extractTrackNumber(originalTrackNumber: Int) : Int {
    if (originalTrackNumber >= 1000){
        return originalTrackNumber % 1000
    }
    return originalTrackNumber
}
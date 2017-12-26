package dev.olog.data.mapper

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dev.olog.data.DataConstants
import dev.olog.data.utils.getInt
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getString
import dev.olog.domain.entity.UneditedSong
import java.io.File

private val COVER_URI = Uri.parse("content://media/external/audio/albumart")

fun Cursor.toUneditedSong(): UneditedSong {
    val albumId = getLong(MediaStore.Audio.Media.ALBUM_ID)

    val path = getString(MediaStore.Audio.Media.DATA)
    val folder = extractFolder(path)
    val album = getString(MediaStore.Audio.Media.ALBUM)

    return UneditedSong(
            getLong(MediaStore.Audio.Media._ID),
            getLong(MediaStore.Audio.Media.ARTIST_ID),
            getLong(MediaStore.Audio.Media.ALBUM_ID),
            getString(MediaStore.Audio.Media.TITLE),
            getString(MediaStore.Audio.Media.ARTIST),
            adjustAlbum(album, folder),
            path,
            getInt(MediaStore.Audio.Media.TRACK),
            getInt(MediaStore.Audio.Media.YEAR),
            ContentUris.withAppendedId(COVER_URI, albumId).toString(),
            getLong(MediaStore.Audio.Media.DURATION),
            getLong(MediaStore.Audio.Media.SIZE)
    )
}

private fun extractFolder(path: String): String {
    val lastSep = path.lastIndexOf(File.separator)
    val prevSep = path.lastIndexOf(File.separator, lastSep - 1)
    return path.substring(prevSep + 1, lastSep)
}

private fun adjustAlbum(album: String, folder: String): String {
    val hasUnknownAlbum = album == DataConstants.UNKNOWN || album == folder
    return if (hasUnknownAlbum){
        DataConstants.UNKNOWN
    } else album
}
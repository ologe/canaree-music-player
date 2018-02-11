package dev.olog.msc.data.mapper

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.database.getInt
import androidx.database.getLong
import androidx.database.getString
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.UneditedSong
import dev.olog.msc.utils.img.ImagesFolderUtils
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
            ImagesFolderUtils.getOriginalAlbumCover(albumId).toString(),
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
    val hasUnknownAlbum = album == AppConstants.UNKNOWN || album == folder
    return if (hasUnknownAlbum){
        AppConstants.UNKNOWN
    } else album
}
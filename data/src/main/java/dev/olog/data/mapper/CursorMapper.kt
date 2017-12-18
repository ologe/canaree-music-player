package dev.olog.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getString
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import java.io.File

fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLong(android.provider.BaseColumns._ID)

    val image = "${context.applicationInfo.dataDir}${File.separator}genre"
    val file = File(image)
    val imageFile = if (file.exists()){
        file.listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == "$id" }
    } else null

    return Genre(
            id,
            this.getString(MediaStore.Audio.GenresColumns.NAME),
            genreSize,
            if (imageFile != null) imageFile.path else ""
    )
}

fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLong(android.provider.BaseColumns._ID)

    val image = "${context.applicationInfo.dataDir}${File.separator}playlist"
    val file = File(image)
    val imageFile = if (file.exists()){
        file.listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == "$id" }
    } else null

    return Playlist(
            id,
            this.getString(MediaStore.Audio.PlaylistsColumns.NAME),
            playlistSize,
            if (imageFile != null) imageFile.path else ""
    )
}

fun Cursor.extractId() : Long {
    return this.getLong(android.provider.BaseColumns._ID)
}
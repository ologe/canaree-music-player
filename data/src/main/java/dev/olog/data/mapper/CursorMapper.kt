package dev.olog.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.data.utils.FileUtils
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getString
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import java.io.File

fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLong(android.provider.BaseColumns._ID)
    val image = FileUtils.genreImagePath(context, id)
    val file = File(image)

    return Genre(
            id,
            this.getString(MediaStore.Audio.GenresColumns.NAME),
            genreSize,
            if (file.exists()) image else ""
    )
}

fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLong(android.provider.BaseColumns._ID)
    val image = FileUtils.playlistImagePath(context, id)
    val file = File(image)

    return Playlist(
            id,
            this.getString(MediaStore.Audio.PlaylistsColumns.NAME),
            playlistSize,
            if (file.exists()) image else ""
    )
}

fun Cursor.extractId() : Long {
    return this.getLong(android.provider.BaseColumns._ID)
}
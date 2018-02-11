package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.getLong
import dev.olog.msc.utils.k.extension.getString

fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLong(android.provider.BaseColumns._ID)
    return Genre(
            id,
            this.getString(MediaStore.Audio.GenresColumns.NAME),
            genreSize,
            ImagesFolderUtils.forGenre(context, id)
    )
}

fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLong(android.provider.BaseColumns._ID)

    return Playlist(
            id,
            this.getString(MediaStore.Audio.PlaylistsColumns.NAME),
            playlistSize,
            ImagesFolderUtils.forPlaylist(context, id)
    )
}

fun Cursor.extractId() : Long {
    return this.getLong(android.provider.BaseColumns._ID)
}
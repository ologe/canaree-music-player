package dev.olog.data.mapper

import android.database.Cursor
import android.provider.MediaStore
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getString
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist

fun Cursor.toGenre() : Genre {
    return Genre(
            this.getLong(android.provider.BaseColumns._ID),
            this.getString(MediaStore.Audio.GenresColumns.NAME)
    )
}

fun Cursor.toPlaylist() : Playlist {
    return Playlist(
            this.getLong(android.provider.BaseColumns._ID),
            this.getString(MediaStore.Audio.PlaylistsColumns.NAME)
    )
}

fun Cursor.extractId() : Long {
    return this.getLong(android.provider.BaseColumns._ID)
}
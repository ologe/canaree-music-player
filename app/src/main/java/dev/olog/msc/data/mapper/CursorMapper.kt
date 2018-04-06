package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.core.database.getLong
import androidx.core.database.getString
import dev.olog.msc.data.entity.PlaylistSongEntity
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.utils.img.ImagesFolderUtils

fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLong(android.provider.BaseColumns._ID)
    return Genre(
            id,
            this.getString(MediaStore.Audio.GenresColumns.NAME).capitalize(),
            genreSize,
            ImagesFolderUtils.forGenre(context, id)
    )
}

fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLong(android.provider.BaseColumns._ID)

    return Playlist(
            id,
            this.getString(MediaStore.Audio.PlaylistsColumns.NAME).capitalize(),
            playlistSize,
            ImagesFolderUtils.forPlaylist(context, id)
    )
}

fun Cursor.extractId() : Long {
    return this.getLong(android.provider.BaseColumns._ID)
}

fun Cursor.toPlaylistSong() : PlaylistSongEntity {
    return PlaylistSongEntity(
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members._ID),
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID)
    )
}
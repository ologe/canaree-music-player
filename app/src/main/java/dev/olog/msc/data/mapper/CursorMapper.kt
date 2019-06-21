package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.core.entity.Genre
import dev.olog.core.entity.Playlist
import dev.olog.msc.data.entity.PlaylistSongEntity
import dev.olog.msc.utils.getLong
import dev.olog.msc.utils.getLongOrNull
import dev.olog.msc.utils.getStringOrNull

fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.GenresColumns.NAME)?.capitalize() ?: ""
    return Genre(
        id,
        name,
        genreSize
    )
}

fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.PlaylistsColumns.NAME)?.capitalize() ?: ""

    return Playlist(
        id,
        name,
        playlistSize
    )
}

fun Cursor.extractId() : Long {
    return this.getLong(android.provider.BaseColumns._ID)
}

fun Cursor.toPlaylistSong() : PlaylistSongEntity {
    return PlaylistSongEntity(
            this.getLong(MediaStore.Audio.Playlists.Members._ID),
            this.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
    )
}
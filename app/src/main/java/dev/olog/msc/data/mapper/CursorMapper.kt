package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.core.database.getLong
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import dev.olog.msc.data.entity.PlaylistSongEntity
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.PlaylistType
import dev.olog.msc.utils.img.ImagesFolderUtils

fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.GenresColumns.NAME)?.capitalize() ?: ""
    return Genre(
            id,
            name,
            genreSize,
            ImagesFolderUtils.forGenre(context, id)
    )
}

fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.PlaylistsColumns.NAME)?.capitalize() ?: ""

    return Playlist(
            id,
            name,
            playlistSize,
            ImagesFolderUtils.forPlaylist(context, id)
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
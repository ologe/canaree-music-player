package dev.olog.data.mapper

import android.database.Cursor
import dev.olog.data.entity.PlaylistSongEntity
import dev.olog.data.utils.getLong

fun Cursor.toPlaylistSong() : PlaylistSongEntity {
    return PlaylistSongEntity(
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members._ID),
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID)
    )
}
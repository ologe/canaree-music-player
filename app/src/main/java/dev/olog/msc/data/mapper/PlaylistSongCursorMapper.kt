package dev.olog.msc.data.mapper

import android.database.Cursor
import dev.olog.msc.data.entity.PlaylistSongEntity
import dev.olog.msc.utils.k.extension.getLong

fun Cursor.toPlaylistSong() : PlaylistSongEntity {
    return PlaylistSongEntity(
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members._ID),
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID)
    )
}
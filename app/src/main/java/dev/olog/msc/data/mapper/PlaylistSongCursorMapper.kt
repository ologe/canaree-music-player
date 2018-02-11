package dev.olog.msc.data.mapper

import android.database.Cursor
import androidx.database.getLong
import dev.olog.msc.data.entity.PlaylistSongEntity

fun Cursor.toPlaylistSong() : PlaylistSongEntity {
    return PlaylistSongEntity(
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members._ID),
            this.getLong(android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID)
    )
}
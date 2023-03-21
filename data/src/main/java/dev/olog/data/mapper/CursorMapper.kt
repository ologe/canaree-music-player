@file:Suppress("DEPRECATION")

package dev.olog.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.core.entity.track.*
import dev.olog.data.utils.getInt
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getStringOrNull

fun Cursor.toSong(): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getStringOrNull(MediaStore.MediaColumns.DATA) ?: ""

    val title = getStringOrNull(MediaStore.MediaColumns.TITLE) ?: ""

    val artist = getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST) ?: ""
    val album = getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM) ?: ""

    val albumArtist = getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM_ARTIST) ?: artist

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val track = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val isPodcast = getLong(MediaStore.Audio.AudioColumns.IS_PODCAST) != 0L

    return Song(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration,
        dateAdded = dateAdded,
        path = path,
        trackColumn = track,
        idInPlaylist = -1,
        isPodcast = isPodcast
    )
}

fun Cursor.toPlaylistSong(): Song {
    val idInPlaylist = getInt(MediaStore.Audio.Playlists.Members._ID)
    val id = getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getStringOrNull(MediaStore.MediaColumns.DATA) ?: ""

    val title = getStringOrNull(MediaStore.MediaColumns.TITLE) ?: ""

    val artist = getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST) ?: ""
    val album = getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM) ?: ""

    val albumArtist = getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM_ARTIST) ?: artist

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val track = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val isPodcast = getLong(MediaStore.Audio.AudioColumns.IS_PODCAST) != 0L

    return Song(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration,
        dateAdded = dateAdded,
        path = path,
        trackColumn = track,
        idInPlaylist = idInPlaylist,
        isPodcast = isPodcast
    )
}
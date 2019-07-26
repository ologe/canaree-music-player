package dev.olog.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.core.entity.track.*
import dev.olog.data.queries.Columns
import dev.olog.data.utils.getInt
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getString
import dev.olog.data.utils.getStringOrNull
import java.io.File

fun Cursor.toSong(): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getStringOrNull(MediaStore.MediaColumns.DATA) ?: ""

    val title = getString(MediaStore.MediaColumns.TITLE)

    val artist = getString(MediaStore.Audio.AudioColumns.ARTIST)
    val album = getString(MediaStore.Audio.AudioColumns.ALBUM)

    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)
    val dateModified = getLong(MediaStore.MediaColumns.DATE_MODIFIED)

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
        dateModified = dateModified,
        path = path,
        trackColumn = track,
        idInPlaylist = -1,
        isPodcast = isPodcast
    )
}

fun Cursor.toAlbum(): Album {
    val title = getString(MediaStore.Audio.Media.ALBUM)
    val artist = getString(MediaStore.Audio.Media.ARTIST)
    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist

    val data = getString(MediaStore.Audio.AudioColumns.DATA)
    val path = data.substring(1, data.lastIndexOf(File.separator))
    val dirName = path.substring(path.lastIndexOf(File.separator) + 1)
    val isPodcast = getLong(MediaStore.Audio.AudioColumns.IS_PODCAST) != 0L

    return Album(
        id = getLong(MediaStore.Audio.Media.ALBUM_ID),
        artistId = getLong(MediaStore.Audio.Media.ARTIST_ID),
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        songs = 0,
        hasSameNameAsFolder = dirName == title,
        isPodcast = isPodcast
    )
}

fun Cursor.toArtist(): Artist {
    val artist = getString(MediaStore.Audio.Media.ARTIST)
    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist
    val isPodcast = getLong(MediaStore.Audio.AudioColumns.IS_PODCAST) != 0L

    return Artist(
        id = getLong(MediaStore.Audio.Media.ARTIST_ID),
        name = artist,
        albumArtist = albumArtist,
        songs = 0,
        albums = 0,
        isPodcast = isPodcast
    )
}

internal fun Cursor.toPlaylist(): Playlist {
    val id = getLong(BaseColumns._ID)
    val name = getString(MediaStore.Audio.PlaylistsColumns.NAME).capitalize()

    return Playlist(
        id = id,
        title = name,
        size = 0, // wil be updated later
        isPodcast = false
    )
}

internal fun Cursor.toGenre(): Genre {
    val id = this.getLong(BaseColumns._ID)
    val name = this.getString(MediaStore.Audio.GenresColumns.NAME).capitalize()
    return Genre(
        id = id,
        name = name,
        size = 0 // wil be updated later
    )
}
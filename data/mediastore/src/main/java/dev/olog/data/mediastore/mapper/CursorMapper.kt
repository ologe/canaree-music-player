@file:Suppress("DEPRECATION")

package dev.olog.data.mediastore.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.domain.entity.track.*
import dev.olog.domain.gateway.base.Id
import dev.olog.data.mediastore.queries.Columns
import dev.olog.data.mediastore.utils.getInt
import dev.olog.data.mediastore.utils.getLong
import dev.olog.data.mediastore.utils.getStringOrNull
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.time.milliseconds

internal fun Cursor.toTrack(): Track.Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getStringOrNull(MediaStore.MediaColumns.DATA) ?: ""

    val title = getStringOrNull(MediaStore.MediaColumns.TITLE) ?: ""

    val artist = getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST) ?: ""
    val album = getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM) ?: ""

    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)
    val dateModified = getLong(MediaStore.MediaColumns.DATE_MODIFIED)

    val track = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val isPodcast = getLong(MediaStore.Audio.AudioColumns.IS_PODCAST) != 0L

    return Track.Song(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration.milliseconds,
        dateAdded = dateAdded,
        dateModified = dateModified,
        path = path,
        trackColumn = track,
        isPodcast = isPodcast
    )
}

internal fun Cursor.toPlaylistTrack(playlistId: Long): Track.PlaylistSong {
    val idInPlaylist = getLong(MediaStore.Audio.Playlists.Members._ID)
    val id = getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getStringOrNull(MediaStore.MediaColumns.DATA) ?: ""

    val title = getStringOrNull(MediaStore.MediaColumns.TITLE) ?: ""

    val artist = getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST) ?: ""
    val album = getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM) ?: ""

    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)
    val dateModified = getLong(MediaStore.MediaColumns.DATE_MODIFIED)

    val track = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val isPodcast = getLong(MediaStore.Audio.AudioColumns.IS_PODCAST) != 0L

    return Track.PlaylistSong(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration.milliseconds,
        dateAdded = dateAdded,
        dateModified = dateModified,
        path = path,
        trackColumn = track,
        isPodcast = isPodcast,
        playlistId = playlistId,
        idInPlaylist = idInPlaylist,
    )
}

internal fun Cursor.toAlbum(): Album {
    val title = getStringOrNull(MediaStore.Audio.Media.ALBUM) ?: ""
    val artist = getStringOrNull(MediaStore.Audio.Media.ARTIST) ?: ""
    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist

    val data = getStringOrNull(MediaStore.Audio.AudioColumns.DATA) ?: ""
    val dirName = try {
        val path = data.substring(1, data.lastIndexOf(File.separator))
        path.substring(path.lastIndexOf(File.separator) + 1)
    } catch (ex: Throwable) {
        Timber.w("invalid path=$data")
        ""
    }
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

internal fun Cursor.toArtist(): Artist {
    val artist = getStringOrNull(MediaStore.Audio.Media.ARTIST) ?: ""
    val albumArtist = getStringOrNull(Columns.ALBUM_ARTIST) ?: artist
    val isPodcast = getLong(MediaStore.Audio.AudioColumns.IS_PODCAST) != 0L

    return Artist(
        id = getLong(MediaStore.Audio.Media.ARTIST_ID),
        name = artist,
        albumArtist = albumArtist,
        songs = 0,
        isPodcast = isPodcast
    )
}

internal suspend fun Cursor.toGenre(computeSize: suspend (Id) -> Int?): Genre? {
    val id = this.getLong(BaseColumns._ID)
    val name = this.getStringOrNull(MediaStore.Audio.GenresColumns.NAME)?.capitalize(Locale.ROOT) ?: ""

    val size = computeSize(id) ?: return null
    return Genre(
        id = id,
        name = name,
        size = size
    )
}
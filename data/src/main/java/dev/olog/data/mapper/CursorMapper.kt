package dev.olog.data.mapper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dev.olog.data.utils.FileUtils
import dev.olog.data.utils.getInt
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getString
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import java.io.File

private val COVER_URI = Uri.parse("content://media/external/audio/albumart")

fun Cursor.toGenre() : Genre {
    return Genre(
            this.getLong(android.provider.BaseColumns._ID),
            this.getString(MediaStore.Audio.GenresColumns.NAME)
    )
}

fun Cursor.toArtist(context: Context) : Artist {
    val id = this.getLong(MediaStore.Audio.Artists._ID)
    val image = FileUtils.artistImagePath(context, id)
    val file = File(image)

    return Artist(
            id,
            this.getString(MediaStore.Audio.Artists.ARTIST),
            this.getInt(MediaStore.Audio.Artists.NUMBER_OF_TRACKS),
            this.getInt(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS),
            if (file.exists()) image else ""
    )
}

fun Cursor.toArtistsAlbum(artistId: Long) : Album {
    val albumId = this.getLong(MediaStore.Audio.Artists.Albums.ALBUM_ID)

    return Album(
            this.getLong(MediaStore.Audio.Artists.Albums.ALBUM_ID),
            artistId,
            this.getString(MediaStore.Audio.Artists.Albums.ALBUM),
            this.getString(MediaStore.Audio.Artists.Albums.ARTIST),
            ContentUris.withAppendedId(COVER_URI, albumId).toString()
    )
}

fun Cursor.toAlbum() : Album {
    val id = this.getLong(MediaStore.Audio.AlbumColumns.ALBUM_ID)

    return Album(
            id,
            -1,
            this.getString(MediaStore.Audio.AlbumColumns.ALBUM),
            this.getString(MediaStore.Audio.AlbumColumns.ARTIST),
            ContentUris.withAppendedId(COVER_URI, id).toString()
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
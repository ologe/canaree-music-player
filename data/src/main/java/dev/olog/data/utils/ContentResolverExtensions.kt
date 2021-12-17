package dev.olog.data.utils

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import dev.olog.data.index.*
import dev.olog.shared.titlecase
import java.io.File

internal fun Cursor.mapToIndexedPlayables(): List<Indexed_playables> = use {
    val idColumn = getColumnIndex(MediaStore.Audio.AudioColumns._ID)
    val authorIdColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val collectionIdColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)
    val pathColumn = getColumnIndex(MediaStore.MediaColumns.DATA)
    val titleColumn = getColumnIndex(MediaStore.MediaColumns.TITLE)
    val authorColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
    val collectionColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)
    val albumArtistColumn = getColumnIndex(Columns.ALBUM_ARTIST)
    val durationColumn = getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
    val dateAddedColumn = getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)
    val trackColumn = getColumnIndex(MediaStore.Audio.AudioColumns.TRACK)
    val isPodcastColumn = getColumnIndex(MediaStore.Audio.AudioColumns.IS_PODCAST)

    buildList {
        while (moveToNext()) {
            val author = getStringOrNull(authorColumn) ?: ""
            val track = getIntOrNull(trackColumn) ?: 0
            val path = getStringOrNull(pathColumn) ?: ""
            val item = Indexed_playables(
                id = getLongOrNull(idColumn) ?: continue,
                author_id = getLongOrNull(authorIdColumn) ?: continue,
                collection_id = getLongOrNull(collectionIdColumn) ?: continue,
                title = getStringOrNull(titleColumn) ?: "",
                author = author,
                collection = getStringOrNull(collectionColumn) ?: "",
                album_artist = getStringOrNull(albumArtistColumn) ?: author,
                duration = getLongOrNull(durationColumn) ?: -1L,
                date_added = getLongOrNull(dateAddedColumn) ?: -1L,
                path = path,
                directory = if (path.isNotBlank()) {
                path.substring(0, path.lastIndexOf(File.separator).coerceAtLeast(0))
                } else {
                    ""
                },
                disc_number = if (track >= 1000) track / 1000 else 0,
                track_number = if (track >= 1000) track % 1000 else track,
                is_podcast = getIntOrNull(isPodcastColumn) != 0
            )
            add(item)
        }
    }
}

internal fun Cursor.mapToIndexedGenres(): List<Indexed_genres> = use {
    val idColumn = getColumnIndex(BaseColumns._ID)
    val nameColumn = getColumnIndex(MediaStore.Audio.GenresColumns.NAME)

    buildList {
        while (moveToNext()) {
            val id = getLongOrNull(idColumn) ?: continue
            val name = getStringOrNull(nameColumn)?.titlecase() ?: continue
            val item = Indexed_genres(
                id = id,
                name = name
            )
            add(item)
        }
    }
}

internal fun Cursor.mapToIndexedGenrePlayable(genreId: Long): List<Indexed_genres_playables> = use {
    val playableIdColumn = getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID)

    buildList {
        while (moveToNext()) {
            val item = Indexed_genres_playables(
                genreId,
                getLongOrNull(playableIdColumn) ?: continue
            )
            add(item)
        }
    }
}

internal fun Cursor.mapToIndexedPlaylist(): List<Indexed_playlists> = use {
    val idColumn = getColumnIndex(BaseColumns._ID)
    val titleColumn = getColumnIndex(MediaStore.Audio.Playlists.NAME)

    buildList {
        while (moveToNext()) {
            val id = getLongOrNull(idColumn) ?: continue
            val title = getStringOrNull(titleColumn)?.titlecase() ?: continue
            val item = Indexed_playlists(
                id = id,
                title = title
            )
            add(item)
        }
    }
}

internal fun Cursor.mapToIndexedPlaylistPlayable(playlistId: Long): List<Indexed_playlists_playables> = use {
    val playableIdColumn = getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)
    val playOrderColumn = getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)

    buildList {
        while (moveToNext()) {
            val item = Indexed_playlists_playables(
                playlist_id = playlistId,
                playable_id = getLongOrNull(playableIdColumn) ?: continue,
                play_order = getLongOrNull(playOrderColumn) ?: continue
            )
            add(item)
        }
    }
}
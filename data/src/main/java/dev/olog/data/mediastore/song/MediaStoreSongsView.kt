package dev.olog.data.mediastore.song

import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Song
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_SONGS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION

@DatabaseView("""
SELECT mediastore_audio.*
FROM mediastore_audio
    LEFT JOIN blacklist ON mediastore_audio.directory = blacklist.directory --remove blacklisted
WHERE blacklist.directory IS NULL AND isPodcast = false
ORDER BY lower(title) COLLATE UNICODE ASC
""", viewName = "songs_view")
data class MediaStoreSongsView(
    val id: String,
    val artistId: String,
    val albumId: String,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val directory: String,
    val path: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean,
    val displayName: String,
)

@DatabaseView("""
SELECT songs_view.*
FROM songs_view LEFT JOIN sort ON TRUE
WHERE sort.tableName = '${SORT_TABLE_SONGS}'
ORDER BY
-- artist, then title
CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND artist = '$UNKNOWN_STRING' THEN -1 END,
CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(artist) COLLATE UNICODE END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(artist) COLLATE UNICODE END DESC,
-- album, then title
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND album = '$UNKNOWN_STRING' THEN -1 END,
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(album) COLLATE UNICODE END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(album) COLLATE UNICODE END DESC,
-- duration, then title
CASE WHEN sort.columnName = '${SORT_TYPE_DURATION}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN duration END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_DURATION}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN duration END DESC,
-- date, then title
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN dateAdded END DESC,
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN dateAdded END ASC,
-- default, and second sort
CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(title) COLLATE UNICODE END ASC,
CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(title) COLLATE UNICODE END DESC
""", viewName = "songs_view_sorted")
data class MediaStoreSongsViewSorted(
    val id: String,
    val artistId: String,
    val albumId: String,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val directory: String,
    val path: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean,
    val displayName: String,
)

fun MediaStoreSongsView.toDomain(): Song {
    return Song(
        id = id.toLong(),
        artistId = artistId.toLong(),
        albumId = albumId.toLong(),
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration,
        dateAdded = dateAdded,
        dateModified = dateModified,
        directory = directory,
        path = path,
        discNumber = discNumber,
        trackNumber = trackNumber,
        idInPlaylist = -1,
        isPodcast = isPodcast,
    )
}

fun MediaStoreSongsViewSorted.toDomain(): Song {
    return Song(
        id = id.toLong(),
        artistId = artistId.toLong(),
        albumId = albumId.toLong(),
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration,
        dateAdded = dateAdded,
        dateModified = dateModified,
        directory = directory,
        path = path,
        discNumber = discNumber,
        trackNumber = trackNumber,
        idInPlaylist = -1,
        isPodcast = isPodcast,
    )
}
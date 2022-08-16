package dev.olog.data.mediastore.podcast.album

import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Album
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_PODCAST_ALBUMS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE

@DatabaseView("""
SELECT DISTINCT albumId AS id, artistId, album AS title, artist, albumArtist, count(*) AS songs, MIN(dateAdded) as dateAdded, directory
FROM podcasts_view
GROUP BY albumId
""", viewName = "podcast_albums_view")
data class MediaStorePodcastAlbumsView(
    val id: String,
    val artistId: String,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val songs: Int,
    val dateAdded: Long,
    val directory: String,
)

@DatabaseView("""
SELECT podcast_albums_view.*
FROM podcast_albums_view LEFT JOIN sort ON TRUE
WHERE sort.tableName = '$SORT_TABLE_PODCAST_ALBUMS'
ORDER BY
-- title, then artist
CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND title = '$UNKNOWN_STRING' THEN -1 END,
-- artist, then artist
CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND artist = '$UNKNOWN_STRING' THEN -1 END,
CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(artist) END COLLATE UNICODE ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(artist) END COLLATE UNICODE DESC,
-- date, then artist
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN dateAdded END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN dateAdded END DESC,
-- default, and second sort
CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(title) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(title) END COLLATE UNICODE DESC
""", viewName = "podcast_albums_view_sorted")
data class MediaStorePodcastAlbumsViewSorted(
    val id: String,
    val artistId: String,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val songs: Int,
    val dateAdded: Long,
    val directory: String,
)

fun MediaStorePodcastAlbumsView.toDomain(): Album {
    return Album(
        id = id.toLong(),
        artistId = artistId.toLong(),
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        songs = songs,
        directory = directory,
        isPodcast = false,
    )
}

fun MediaStorePodcastAlbumsViewSorted.toDomain(): Album {
    return Album(
        id = id.toLong(),
        artistId = artistId.toLong(),
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        songs = songs,
        directory = directory,
        isPodcast = false,
    )
}
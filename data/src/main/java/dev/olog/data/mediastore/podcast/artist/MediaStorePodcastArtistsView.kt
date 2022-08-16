package dev.olog.data.mediastore.podcast.artist

import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Artist
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_PODCAST_ARTISTS
import dev.olog.data.sort.db.SORT_TYPE_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE

@DatabaseView("""
SELECT DISTINCT artistId AS id, artist AS name, count(*) AS songs, MIN(dateAdded) as dateAdded 
FROM podcasts_view
GROUP BY artistId
""", viewName = "podcast_artists_view")
data class MediaStorePodcastArtistsView(
    val id: String,
    val name: String,
    val songs: Int,
    val dateAdded: Long,
)

@DatabaseView("""
SELECT podcast_artists_view.*
FROM podcast_artists_view LEFT JOIN sort ON TRUE
WHERE sort.tableName = '${SORT_TABLE_PODCAST_ARTISTS}'
ORDER BY
-- artist
CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND name = '${UNKNOWN_STRING}' THEN -1 END,
-- date, then artist
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN dateAdded END ASC,
CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN dateAdded END DESC,
-- default, and second sort
CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(name) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(name) END COLLATE UNICODE DESC
""", viewName = "podcast_artists_view_sorted")
data class MediaStorePodcastArtistsViewSorted(
    val id: String,
    val name: String,
    val songs: Int,
    val dateAdded: Long,
)

fun MediaStorePodcastArtistsView.toDomain(): Artist {
    return Artist(
        id = id.toLong(),
        name = name,
        songs = songs,
        isPodcast = false,
    )
}

fun MediaStorePodcastArtistsViewSorted.toDomain(): Artist {
    return Artist(
        id = id.toLong(),
        name = name,
        songs = songs,
        isPodcast = false,
    )
}
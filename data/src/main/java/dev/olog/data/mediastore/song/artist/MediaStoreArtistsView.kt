package dev.olog.data.mediastore.song.artist

import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Artist
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_ARTISTS

@DatabaseView("""
SELECT DISTINCT artistId AS id, artist AS name, count(*) AS songs, MIN(dateAdded) as dateAdded 
FROM songs_view
GROUP BY artistId
ORDER BY lower(name) COLLATE UNICODE ASC
""", viewName = "artists_view")
data class MediaStoreArtistsView(
    val id: String,
    val name: String,
    val songs: Int,
    val dateAdded: Long,
)

@DatabaseView("""
SELECT artists_view.*
FROM artists_view
    LEFT JOIN sort ON TRUE -- join with sort to observe table, keep on TRUE so WHERE clause is working
WHERE sort.tableName = '${SORT_TABLE_ARTISTS}'
ORDER BY
-- author
CASE WHEN name = '${UNKNOWN_STRING}' THEN -1 END, -- when unknown move last
CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(name) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(name) END COLLATE UNICODE DESC
""", viewName = "artists_view_sorted")
data class MediaStoreArtistsViewSorted(
    val id: String,
    val name: String,
    val songs: Int,
    val dateAdded: Long,
)

fun MediaStoreArtistsView.toDomain(): Artist {
    return Artist(
        id = id.toLong(),
        name = name,
        songs = songs,
        isPodcast = false,
    )
}

fun MediaStoreArtistsViewSorted.toDomain(): Artist {
    return Artist(
        id = id.toLong(),
        name = name,
        songs = songs,
        isPodcast = false,
    )
}
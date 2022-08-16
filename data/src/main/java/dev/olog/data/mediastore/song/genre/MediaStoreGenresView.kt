package dev.olog.data.mediastore.song.genre

import androidx.room.DatabaseView
import dev.olog.core.entity.track.Genre
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_FOLDERS
import dev.olog.data.sort.db.SORT_TABLE_GENRES

@DatabaseView("""
SELECT mediastore_genre.*, COUNT(*) AS songs 
FROM mediastore_genre 
    JOIN mediastore_genre_track ON mediastore_genre.id = mediastore_genre_track.genreId
    JOIN songs_view ON mediastore_genre_track.songId = songs_view.id 
GROUP BY mediastore_genre.id
""", viewName = "genres_view")
data class MediaStoreGenresView(
    val id: String,
    val name: String,
    val songs: Int,
)

@DatabaseView("""
SELECT genres_view.* 
FROM genres_view LEFT JOIN sort on TRUE
WHERE sort.tableName = '$SORT_TABLE_GENRES'
ORDER BY
-- title
CASE WHEN sort.direction = '$SORT_DIRECTION_ASC' THEN lower(name) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '$SORT_DIRECTION_DESC' THEN lower(name) END COLLATE UNICODE DESC
""", viewName = "genres_view_sorted")
data class MediaStoreGenresViewSorted(
    val id: String,
    val name: String,
    val songs: Int,
)

fun MediaStoreGenresView.toDomain(): Genre {
    return Genre(
        id = id.toLong(),
        name = name,
        size = songs,
    )
}

fun MediaStoreGenresViewSorted.toDomain(): Genre {
    return Genre(
        id = id.toLong(),
        name = name,
        size = songs,
    )
}
package dev.olog.data.mediastore.song.folder

import androidx.room.DatabaseView
import dev.olog.core.entity.track.Folder
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_FOLDERS

@DatabaseView("""
SELECT DISTINCT directory AS path, directoryName AS name, count(*) AS songs, MIN(dateAdded) as dateAdded
FROM songs_view
GROUP BY directory
""", viewName = "folders_view")
data class MediaStoreFoldersView(
    val path: String,
    val name: String,
    val songs: Int,
    val dateAdded: Long,
)

@DatabaseView("""
SELECT folders_view.*
FROM folders_view LEFT JOIN sort ON TRUE
WHERE sort.tableName = '${SORT_TABLE_FOLDERS}'
ORDER BY
-- title
CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(name) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(name) END COLLATE UNICODE DESC
""", viewName = "folders_view_sorted")
data class MediaStoreFoldersViewSorted(
    val path: String,
    val name: String,
    val songs: Int,
    val dateAdded: Long,
)

fun MediaStoreFoldersView.toDomain(): Folder {
    return Folder(
        title = name,
        path = path,
        size = songs,
    )
}

fun MediaStoreFoldersViewSorted.toDomain(): Folder {
    return Folder(
        title = name,
        path = path,
        size = songs,
    )
}
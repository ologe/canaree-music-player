package dev.olog.data.mediastore.song.playlist

import androidx.room.DatabaseView
import dev.olog.core.entity.track.Playlist
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_PLAYLISTS

@DatabaseView("""
SELECT mediastore_playlist.*, COUNT(*) AS songs 
FROM mediastore_playlist 
    JOIN mediastore_playlist_track ON mediastore_playlist.id = mediastore_playlist_track.playlistId
    JOIN songs_view ON mediastore_playlist_track.songId = songs_view.id 
GROUP BY mediastore_playlist.id
""", viewName = "playlists_view")
data class MediaStorePlaylistsView(
    val id: String,
    val title: String,
    val path: String,
    val songs: Int,
)

@DatabaseView("""
SELECT playlists_view.* 
FROM playlists_view LEFT JOIN sort on TRUE
WHERE sort.tableName = '$SORT_TABLE_PLAYLISTS'
ORDER BY
-- title
CASE WHEN sort.direction = '$SORT_DIRECTION_ASC' THEN lower(title) END COLLATE UNICODE ASC,
CASE WHEN sort.direction = '$SORT_DIRECTION_DESC' THEN lower(title) END COLLATE UNICODE DESC
""", viewName = "playlists_view_sorted")
data class MediaStorePlaylistsViewSorted(
    val id: String,
    val title: String,
    val path: String,
    val songs: Int,
)

fun MediaStorePlaylistsView.toDomain(): Playlist {
    return Playlist(
        id = id,
        title = title,
        path = path,
        size = songs,
        isPodcast = false,
    )
}

fun MediaStorePlaylistsViewSorted.toDomain(): Playlist {
    return Playlist(
        id = id,
        title = title,
        path = path,
        size = songs,
        isPodcast = false,
    )
}
package dev.olog.data.mediastore.playlist

import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Playlist
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDirectoryEntity.Companion.ID_PRE_ANDROID_Q

private const val SONG_COUNT = "count(CASE WHEN is_podcast = 0 THEN 1 else null END)"
private const val PODCAST_COUNT = "count(CASE WHEN is_podcast <> 0 THEN 1 else null END)"
// can't really distinguish between mediastore track and podcast playlist,
// so mark as podcast if majority of tracks are podcasts, otherwise mark as track
private const val IS_PODCAST = "CASE WHEN ($SONG_COUNT) >= ($PODCAST_COUNT) THEN 0 ELSE 1 END AS is_podcast"

@Suppress("deprecation")
@DatabaseView("""
SELECT playlists._id, playlists.name, playlists._data, count(members._id) as size, $IS_PODCAST 
FROM mediastore_playlist_internal AS playlists 
    LEFT JOIN mediastore_playlist_members_internal AS members ON playlists._id = members.playlist_id
    LEFT JOIN mediastore_audio AS audio ON audio._id = members.audio_id
    JOIN playlist_directory -- playlist_directory must contain only 1 row, so just append path for computation 
WHERE CASE 
    WHEN (playlist_directory.id = $ID_PRE_ANDROID_Q) THEN 1 -- 1 mean true, no filter here
    ELSE playlists._data LIKE playlist_directory.path || '%'
END
GROUP BY playlists._id
""", viewName = "mediastore_playlists")
data class MediaStorePlaylistEntity(
    @ColumnInfo(name = Playlists._ID)
    val id: Long,
    @ColumnInfo(name = Playlists.NAME)
    val title: String,
    @ColumnInfo(name = Playlists.DATA)
    val path: String?,
    val size: Int,
    @ColumnInfo(name = "is_podcast")
    val isPodcast: Int,
)

fun MediaStorePlaylistEntity.toPlaylist(): Playlist {
    return Playlist(
        id = id,
        title = title,
        path = path,
        size = size,
        isPodcast = isPodcast != 0,
    )
}
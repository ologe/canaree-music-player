package dev.olog.data.mediastore.artist

import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Artist

@DatabaseView("""
SELECT artist_id, artist, album_artist, is_podcast, count(*) as size, MAX(date_added) AS ${AudioColumns.DATE_ADDED}
FROM mediastore_audio
WHERE artist <> '${UNKNOWN_STRING}'
GROUP BY artist_id
""", viewName = "mediastore_artists")
/**
 * keep in sync with similar queries:
 *   [dev.olog.data.mediastore.folder.MediaStoreFolderDao.observeRelatedArtists]
 */
data class MediaStoreArtistEntity(
    @ColumnInfo(name = AudioColumns.ARTIST_ID)
    val id: Long,
    @ColumnInfo(name = AudioColumns.ARTIST)
    val name: String?,
    @ColumnInfo(name = AudioColumns.ALBUM_ARTIST)
    val albumArtist: String?,
    @ColumnInfo(name = AudioColumns.IS_PODCAST)
    val isPodcast: Boolean,
    @ColumnInfo(name = AudioColumns.DATE_ADDED)
    val dateAdded: Long,
    val size: Int,
)

fun MediaStoreArtistEntity.toArtist(): Artist {
    return Artist(
        id = id,
        name = name ?: UNKNOWN_STRING,
        albumArtist = albumArtist ?: UNKNOWN_STRING,
        songs = size,
        isPodcast = isPodcast,
    )
}
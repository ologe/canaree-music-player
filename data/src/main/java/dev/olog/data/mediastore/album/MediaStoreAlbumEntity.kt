package dev.olog.data.mediastore.album

import dev.olog.data.mediastore.columns.AudioColumns
import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Album

@DatabaseView("""
SELECT album_id, artist_id, album, artist, album_artist, is_podcast, count(*) as size, MAX(date_added) AS ${AudioColumns.DATE_ADDED} 
FROM mediastore_audio
WHERE album <> '${UNKNOWN_STRING}' AND album_id <> (
    -- filter out invalid album that use '0' as name from 'storage/emulated/0' device path
    SELECT album_id
    FROM mediastore_audio
    WHERE album = '0' AND bucket_display_name = '${UNKNOWN_STRING}'
)
GROUP BY album_id
""", viewName = "mediastore_albums")
data class MediaStoreAlbumEntity(
    @ColumnInfo(name = AudioColumns.ALBUM_ID)
    val id: Long,
    @ColumnInfo(name = AudioColumns.ARTIST_ID)
    val artistId: Long,
    @ColumnInfo(name = AudioColumns.ALBUM)
    val title: String?,
    @ColumnInfo(name = AudioColumns.ARTIST)
    val artist: String?,
    @ColumnInfo(name = AudioColumns.ALBUM_ARTIST)
    val albumArtist: String?,
    @ColumnInfo(name = AudioColumns.IS_PODCAST)
    val isPodcast: Int,
    @ColumnInfo(name = AudioColumns.DATE_ADDED)
    val dateAdded: Long,
    val size: Int,
)

fun MediaStoreAlbumEntity.toAlbum(): Album {
    return Album(
        id = id,
        artistId = artistId,
        title = title ?: UNKNOWN_STRING,
        artist = artist ?: UNKNOWN_STRING,
        albumArtist = albumArtist ?: UNKNOWN_STRING,
        isPodcast = isPodcast != 0,
        size = size,
    )
}
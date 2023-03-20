package dev.olog.data.mediastore.artist

import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Artist

@DatabaseView("""
SELECT artist_id, artist, album_artist, is_podcast, count(*) as size
FROM mediastore_audio
GROUP BY artist_id
""", viewName = "mediastore_artists")
data class MediaStoreArtistEntity(
    @ColumnInfo(name = AudioColumns.ARTIST_ID)
    val id: Long,
    @ColumnInfo(name = AudioColumns.ARTIST)
    val name: String?,
    @ColumnInfo(name = AudioColumns.ALBUM_ARTIST)
    val albumArtist: String?,
    @ColumnInfo(name = AudioColumns.IS_PODCAST)
    val isPodcast: Boolean,
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
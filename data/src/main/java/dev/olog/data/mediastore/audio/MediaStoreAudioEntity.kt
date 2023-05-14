package dev.olog.data.mediastore.audio

import android.provider.MediaStore
import dev.olog.data.mediastore.columns.AudioColumns
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Song

@DatabaseView("""
SELECT _id, album_id, artist_id, title, 
    CASE -- remove album as folder name when behaviour
        WHEN album = bucket_display_name THEN '${MediaStore.UNKNOWN_STRING}'
        ELSE album
    END AS ${AudioColumns.ALBUM},
    album_artist, artist, 
    bucket_id, bucket_display_name, _data, relative_path, _display_name,
    is_podcast, duration, track, year, date_added, genre_id, genre
FROM mediastore_audio_internal LEFT JOIN blacklist 
    ON mediastore_audio_internal.relative_path = blacklist.directory
WHERE blacklist.directory IS NULL
""", viewName = "mediastore_audio")
data class MediaStoreAudioEntity(
    // ids
    @ColumnInfo(name = AudioColumns._ID)
    val id: Long,
    @ColumnInfo(name = AudioColumns.ALBUM_ID)
    val albumId: Long,
    @ColumnInfo(name = AudioColumns.ARTIST_ID)
    val artistId: Long,

    // basic info
    @ColumnInfo(name = AudioColumns.TITLE)
    val title: String,
    @ColumnInfo(name = AudioColumns.ALBUM)
    val album: String?,
    @ColumnInfo(name = AudioColumns.ALBUM_ARTIST)
    val albumArtist: String?,
    @ColumnInfo(name = AudioColumns.ARTIST)
    val artist: String?,

    // directory/folder
    @ColumnInfo(name = "bucket_id")
    val bucketId: Long, // directory id
    @ColumnInfo(name = "bucket_display_name")
    val bucketDisplayName: String, // directory name
    @ColumnInfo(name = AudioColumns.DATA)
    val data: String?, // full path
    @ColumnInfo(name = "relative_path")
    val relativePath: String, // directory path
    @ColumnInfo(name = AudioColumns.DISPLAY_NAME)
    val displayName: String, // file name

    // audio type
    @ColumnInfo(name = AudioColumns.IS_PODCAST)
    val isPodcast: Int,

    // duration
    @ColumnInfo(name = AudioColumns.DURATION)
    val duration: Long,

    // more info
    @ColumnInfo(name = AudioColumns.TRACK)
    val track: Int?,
    @ColumnInfo(name = AudioColumns.YEAR)
    val year: Int?,

    // date
    @ColumnInfo(name = AudioColumns.DATE_ADDED)
    val dateAdded: Long,

    // genre
    @ColumnInfo(name = "genre_id")
    val genreId: Long?,
    @ColumnInfo(name = "genre")
    val genre: String?,
)

fun MediaStoreAudioEntity.toSong(): Song {
    return Song(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist ?: MediaStore.UNKNOWN_STRING,
        albumArtist = albumArtist ?: MediaStore.UNKNOWN_STRING,
        album = album ?: MediaStore.UNKNOWN_STRING,
        duration = duration,
        dateAdded = dateAdded,
        path = data.orEmpty(),
        trackColumn = track ?: 0,
        idInPlaylist = 0,
        isPodcast = isPodcast != 0,
    )
}
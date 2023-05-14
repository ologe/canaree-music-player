package dev.olog.data.mediastore

import dev.olog.data.mediastore.columns.AudioColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "mediastore_audio_internal",
    primaryKeys = [AudioColumns._ID],
    indices = [
        Index(AudioColumns._ID),
        Index(AudioColumns.ALBUM_ID),
        Index(AudioColumns.ARTIST_ID),
        Index("bucket_id"),
        Index("genre_id"),
    ]
)
data class MediaStoreAudioInternalEntity(
    // ids
    @ColumnInfo(name = AudioColumns._ID)
    val id: Long,
    @ColumnInfo(name = AudioColumns.ALBUM_ID)
    val albumId: Long,
    @ColumnInfo(name = AudioColumns.ARTIST_ID)
    val artistId: Long,

    // basic info
    @ColumnInfo(name = AudioColumns.TITLE, collate = ColumnInfo.LOCALIZED)
    val title: String,
    @ColumnInfo(name = AudioColumns.ALBUM, collate = ColumnInfo.LOCALIZED)
    val album: String?,
    @ColumnInfo(name = AudioColumns.ALBUM_ARTIST, collate = ColumnInfo.LOCALIZED)
    val albumArtist: String?,
    @ColumnInfo(name = AudioColumns.ARTIST, collate = ColumnInfo.LOCALIZED)
    val artist: String?,

    // directory/folder
    @ColumnInfo(name = "bucket_id")
    val bucketId: Long, // directory id
    @ColumnInfo(name = "bucket_display_name", collate = ColumnInfo.UNICODE)
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
    @ColumnInfo(name = "genre", collate = ColumnInfo.UNICODE)
    val genre: String?,
)
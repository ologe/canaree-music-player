package dev.olog.data.mediastore

import android.provider.MediaStore.Audio.AudioColumns
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
        Index(AudioColumns.BUCKET_ID),
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
    @ColumnInfo(name = AudioColumns.BUCKET_ID)
    val bucketId: Long, // directory id
    @ColumnInfo(name = AudioColumns.BUCKET_DISPLAY_NAME, collate = ColumnInfo.UNICODE)
    val bucketDisplayName: String, // directory name
    @ColumnInfo(name = AudioColumns.DATA)
    val data: String?, // full path
    @ColumnInfo(name = AudioColumns.RELATIVE_PATH)
    val relativePath: String, // directory path
    @ColumnInfo(name = AudioColumns.DISPLAY_NAME)
    val displayName: String, // file name

    // audio type
    @ColumnInfo(name = AudioColumns.IS_PODCAST)
    val isPodcast: Boolean,

    // duration
    @ColumnInfo(name = AudioColumns.BOOKMARK)
    val bookmark: Int?, // todo use this
    @ColumnInfo(name = AudioColumns.DURATION)
    val duration: Long,

    // more info
    @ColumnInfo(name = AudioColumns.AUTHOR)
    val author: String?,
    @ColumnInfo(name = AudioColumns.BITRATE)
    val bitrate: Int,
    @ColumnInfo(name = AudioColumns.COMPILATION)
    val compilation: String?,
    @ColumnInfo(name = AudioColumns.COMPOSER)
    val composer: String?,
    @ColumnInfo(name = AudioColumns.SIZE)
    val size: Long, // size in bytes
    @ColumnInfo(name = AudioColumns.TRACK)
    val track: Int?,
    @ColumnInfo(name = AudioColumns.YEAR)
    val year: Int?,
    @ColumnInfo(name = AudioColumns.WRITER)
    val writer: String?,
    @ColumnInfo(name = AudioColumns.IS_FAVORITE)
    val isFavorite: Boolean, // TODO use this?

    // date
    @ColumnInfo(name = AudioColumns.DATE_ADDED)
    val dateAdded: Long,
)
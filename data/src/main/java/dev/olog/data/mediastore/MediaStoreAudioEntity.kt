package dev.olog.data.mediastore

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mediastore_audio",
    indices = [
        Index("id"),
        Index("artistId"),
        Index("albumId"),
        Index("directory"),
    ]
)
data class MediaStoreAudioEntity(
    @PrimaryKey
    val id: String,
    val artistId: String,
    val albumId: String,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val directory: String,
    val directoryName: String,
    val path: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean,
    val displayName: String,
)
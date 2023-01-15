package dev.olog.data.mediastore.audio

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.olog.core.entity.track.Song

@Entity(
    tableName = "mediastore_audio",
    indices = [
        Index("id"),
        Index("artistId"),
        Index("albumId"),
        Index("path"),
        Index("directoryPath"),
    ]
)
data class MediaStoreAudioEntity(
    @PrimaryKey(autoGenerate = false)
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
    val path: String,
    val directoryPath: String,
    val directoryName: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean,
    val displayName: String,
)

fun MediaStoreAudioEntity.toSong(): Song = Song(
    id = id.toLong(),
    artistId = artistId.toLong(),
    albumId = albumId.toLong(),
    title = title,
    artist = artist,
    albumArtist = albumArtist,
    album = album,
    duration = duration,
    dateAdded = dateAdded,
    dateModified = dateModified,
    path = path,
    directoryPath = directoryPath,
    discNumber = discNumber,
    trackNumber = trackNumber,
    isPodcast = isPodcast,
    idInPlaylist = -1,
)
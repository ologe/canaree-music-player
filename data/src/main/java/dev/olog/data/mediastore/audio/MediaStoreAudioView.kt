package dev.olog.data.mediastore.audio

import androidx.room.DatabaseView

@DatabaseView("""
SELECT mediastore_audio.*
FROM mediastore_audio
    LEFT JOIN blacklist ON mediastore_audio.directoryPath = blacklist.directory
WHERE blacklist.directory IS NULL
""", viewName = "mediastore_view")
data class MediaStoreAudioView(
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
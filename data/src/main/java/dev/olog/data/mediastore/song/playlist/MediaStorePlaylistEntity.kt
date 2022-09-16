package dev.olog.data.mediastore.song.playlist

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mediastore_playlist",
    indices = [
        Index("id")
    ]
)
data class MediaStorePlaylistEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val path: String,
)

@Entity(
    tableName = "mediastore_playlist_track",
    indices = [
        Index("playlistId"),
        Index("songId"),
    ],
    primaryKeys = ["playlistId", "songId"]
)
data class MediaStorePlaylistTrackEntity(
    val playlistId: String,
    val songId: String,
    val playOrder: Int,
)
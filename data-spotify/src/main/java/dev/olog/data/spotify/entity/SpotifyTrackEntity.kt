package dev.olog.data.spotify.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "spotify_tracks", indices = [Index("localId")])
data class SpotifyTrackEntity(
    @PrimaryKey val localId: Long,
    val spotifyId: String,
    val duration_ms: Int,
    val explicit: Boolean,
    val name: String,
    val popularity: Int?,
    val previewUrl: String?,
    val trackNumber: Int,
    val discNumber: Int,
    val uri: String,
    // flattened album part
    val image: String,
    val album: String,
    val albumId: String,
    val albumUri: String,
    val albumType: String,
    val releaseDate: String
)
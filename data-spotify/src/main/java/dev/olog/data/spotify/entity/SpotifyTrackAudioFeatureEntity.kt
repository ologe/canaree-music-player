package dev.olog.data.spotify.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "spotify_tracks_audio_feature", indices = [Index("localId")])
data class SpotifyTrackAudioFeatureEntity(
    @PrimaryKey val localId: Long,
    val spotifyId: String,
    val uri: String,
    val acousticness: Double,
    val analysis_url: String,
    val danceability: Double,
    val duration_ms: Int,
    val energy: Double,
    val instrumentalness: Double,
    val key: Int,
    val liveness: Double,
    val loudness: Double,
    val mode: Int,
    val speechiness: Double,
    val tempo: Double,
    val track_href: String,
    val valence: Double
)
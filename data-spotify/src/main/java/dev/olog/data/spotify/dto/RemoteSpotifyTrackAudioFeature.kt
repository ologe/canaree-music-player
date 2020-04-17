package dev.olog.data.spotify.dto

// https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
internal data class RemoteSpotifyTrackAudioFeature(
    val id: String,
    val uri: String,
    val acousticness: Double, // 0-1,
    val analysis_url: String,
    val danceability: Double, // 0-1,
    val duration_ms: Int,
    val energy: Double, // 0-1
    val instrumentalness: Double, // 0-1
    val key: Int, // pitch, -1 is not detected
    val liveness: Double, // 0-1
    val loudness: Double, // typical range between -60 and 0 db
    val mode: Int, // 0 minor, 1 major
    val speechiness: Double, // 0-1
    val tempo: Double, // bpm
    val track_href: String, // link to track full detail
    val valence: Double // 0-1, describing the musical positiveness conveyed by a track
) {

    companion object {

        val EMPTY = RemoteSpotifyTrackAudioFeature(
            id = "",
            uri = "",
            duration_ms = 0,
            acousticness = 0.0,
            analysis_url = "",
            danceability = 0.0,
            energy = 0.0,
            instrumentalness = 0.0,
            key = 0,
            liveness = 0.0,
            loudness = 0.0,
            mode = 0,
            speechiness = 0.0,
            tempo = 0.0,
            track_href = "",
            valence = 0.0
        )

    }

}
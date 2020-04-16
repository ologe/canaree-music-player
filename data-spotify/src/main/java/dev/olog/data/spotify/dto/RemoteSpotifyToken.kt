package dev.olog.data.spotify.dto

internal data class RemoteSpotifyToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)
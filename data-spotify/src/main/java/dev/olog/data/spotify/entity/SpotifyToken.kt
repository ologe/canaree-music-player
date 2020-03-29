package dev.olog.data.spotify.entity

data class SpotifyToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)
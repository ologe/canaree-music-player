package dev.olog.data.spotify.dto

internal data class RemoteSpotifyArtist(
    val followers: RemoteSpotifyFollowers,
    val id: String,
    val images: List<RemoteSpotifyImage>,
    val name: String,
    val popularity: Int,
    val uri: String
)


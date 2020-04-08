package dev.olog.lib.spotify.entity

data class RemoteSpotifyAlbum(
    val album_type: String,
    val id: String,
    val images: List<RemoteSpotifyImage>,
    val name: String,
    val release_date: String,
    val total_tracks: Int,
    val uri: String
)
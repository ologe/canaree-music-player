package dev.olog.data.spotify.entity.complex

import dev.olog.data.spotify.entity.RemoteSpotifyAlbum

data class RemoteSpotifyArtistAlbum(
    val href: String,
    val items: List<RemoteSpotifyAlbum>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)


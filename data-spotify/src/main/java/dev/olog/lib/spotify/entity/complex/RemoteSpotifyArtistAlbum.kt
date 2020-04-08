package dev.olog.lib.spotify.entity.complex

import dev.olog.lib.spotify.entity.RemoteSpotifyAlbum

data class RemoteSpotifyArtistAlbum(
    val href: String,
    val items: List<RemoteSpotifyAlbum>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)


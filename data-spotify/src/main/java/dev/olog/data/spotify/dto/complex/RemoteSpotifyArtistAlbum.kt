package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyAlbum

internal data class RemoteSpotifyArtistAlbum(
    val href: String,
    val items: List<RemoteSpotifyAlbum>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)


package dev.olog.lib.spotify.entity.complex

import dev.olog.lib.spotify.entity.RemoteSpotifyTrack

data class RemoteSpotifyAlbumTracks(
    val href: String,
    val items: List<RemoteSpotifyTrack>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)


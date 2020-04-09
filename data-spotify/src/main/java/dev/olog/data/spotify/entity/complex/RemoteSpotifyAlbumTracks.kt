package dev.olog.data.spotify.entity.complex

import dev.olog.data.spotify.entity.RemoteSpotifyTrack

data class RemoteSpotifyAlbumTracks(
    val href: String,
    val items: List<RemoteSpotifyTrack>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)


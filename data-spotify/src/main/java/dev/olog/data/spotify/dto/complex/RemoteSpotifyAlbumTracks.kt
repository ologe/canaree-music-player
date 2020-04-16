package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyTrack

internal data class RemoteSpotifyAlbumTracks(
    val href: String,
    val items: List<RemoteSpotifyTrack>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)


package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyTrack

internal data class RemoteSpotifyArtistTopTracks(
    val tracks: List<RemoteSpotifyTrack>
)


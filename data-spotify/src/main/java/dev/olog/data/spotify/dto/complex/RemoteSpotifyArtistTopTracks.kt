package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyTrack

data class RemoteSpotifyArtistTopTracks(
    val tracks: List<RemoteSpotifyTrack>
)


package dev.olog.data.spotify.entity.complex

import dev.olog.data.spotify.entity.RemoteSpotifyTrack

data class RemoteSpotifyArtistTopTracks(
    val tracks: List<RemoteSpotifyTrack>
)


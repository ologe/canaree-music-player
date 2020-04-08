package dev.olog.lib.spotify.entity.complex

import dev.olog.lib.spotify.entity.RemoteSpotifyTrack

data class RemoteSpotifyArtistTopTracks(
    val tracks: List<RemoteSpotifyTrack>
)


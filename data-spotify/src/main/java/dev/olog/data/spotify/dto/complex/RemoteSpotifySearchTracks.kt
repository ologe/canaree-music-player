package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyPaging
import dev.olog.data.spotify.dto.RemoteSpotifyTrack

data class RemoteSpotifySearchTracks(
    val tracks: RemoteSpotifyPaging<RemoteSpotifyTrack>
)
package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyPaging
import dev.olog.data.spotify.dto.RemoteSpotifyTrack

internal data class RemoteSpotifySearchTracks(
    val tracks: RemoteSpotifyPaging<RemoteSpotifyTrack>
)
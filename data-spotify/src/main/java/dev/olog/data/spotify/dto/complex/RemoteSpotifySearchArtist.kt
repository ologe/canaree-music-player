package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyArtist
import dev.olog.data.spotify.dto.RemoteSpotifyPaging

internal data class RemoteSpotifySearchArtist(
    val artists: RemoteSpotifyPaging<RemoteSpotifyArtist>
)
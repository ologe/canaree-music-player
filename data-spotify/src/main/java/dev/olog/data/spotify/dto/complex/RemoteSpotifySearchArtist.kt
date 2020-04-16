package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyArtist
import dev.olog.data.spotify.dto.RemoteSpotifyPaging

data class RemoteSpotifySearchArtist(
    val artists: RemoteSpotifyPaging<RemoteSpotifyArtist>
)
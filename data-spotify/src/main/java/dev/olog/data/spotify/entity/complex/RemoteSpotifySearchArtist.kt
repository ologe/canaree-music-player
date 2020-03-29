package dev.olog.data.spotify.entity.complex

import dev.olog.data.spotify.entity.RemoteSpotifyArtist
import dev.olog.data.spotify.entity.RemoteSpotifyPaging

data class RemoteSpotifySearchArtist(
    val artists: RemoteSpotifyPaging<RemoteSpotifyArtist>
)
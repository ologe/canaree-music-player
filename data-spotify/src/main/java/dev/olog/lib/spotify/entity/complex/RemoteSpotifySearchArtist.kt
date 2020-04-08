package dev.olog.lib.spotify.entity.complex

import dev.olog.lib.spotify.entity.RemoteSpotifyArtist
import dev.olog.lib.spotify.entity.RemoteSpotifyPaging

data class RemoteSpotifySearchArtist(
    val artists: RemoteSpotifyPaging<RemoteSpotifyArtist>
)
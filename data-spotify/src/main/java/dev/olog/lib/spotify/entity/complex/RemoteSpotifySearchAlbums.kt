package dev.olog.lib.spotify.entity.complex

import dev.olog.lib.spotify.entity.RemoteSpotifyAlbum
import dev.olog.lib.spotify.entity.RemoteSpotifyPaging

data class RemoteSpotifySearchAlbums(
    val albums: RemoteSpotifyPaging<RemoteSpotifyAlbum>
)
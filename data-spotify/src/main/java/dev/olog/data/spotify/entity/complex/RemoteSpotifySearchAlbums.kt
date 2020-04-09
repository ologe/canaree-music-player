package dev.olog.data.spotify.entity.complex

import dev.olog.data.spotify.entity.RemoteSpotifyAlbum
import dev.olog.data.spotify.entity.RemoteSpotifyPaging

data class RemoteSpotifySearchAlbums(
    val albums: RemoteSpotifyPaging<RemoteSpotifyAlbum>
)
package dev.olog.data.spotify.dto.complex

import dev.olog.data.spotify.dto.RemoteSpotifyAlbum
import dev.olog.data.spotify.dto.RemoteSpotifyPaging

internal data class RemoteSpotifySearchAlbums(
    val albums: RemoteSpotifyPaging<RemoteSpotifyAlbum>
)
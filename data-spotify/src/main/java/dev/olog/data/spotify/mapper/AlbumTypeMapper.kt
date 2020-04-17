package dev.olog.data.spotify.mapper

import dev.olog.domain.entity.spotify.SpotifyAlbumType
import dev.olog.shared.throwNotHandled

internal fun String.mapAlbumType(): SpotifyAlbumType {
    return when (this) {
        "album" -> SpotifyAlbumType.ALBUM
        "single" -> SpotifyAlbumType.SINGLE
        "appears_on" -> SpotifyAlbumType.APPEARS_ON
        "compilation" -> SpotifyAlbumType.COMPILATION
        else -> throwNotHandled(this)
    }
}
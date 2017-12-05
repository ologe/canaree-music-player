package dev.olog.domain.mapper

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Song

fun Song.toAlbum() : Album {
    return Album(
            this.albumId,
            this.artistId,
            this.album,
            this.artist,
            this.image
    )
}
package dev.olog.domain.mapper

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song

fun Song.toFolder() : Folder {
    return Folder(
            this.folder,
            this.folderPath
    )
}

fun Song.toAlbum() : Album {
    return Album(
            this.albumId,
            this.artistId,
            this.album,
            this.artist,
            this.image
    )
}

fun Song.toArtist() : Artist {
    return Artist(
            this.artistId,
            this.artist
    )
}
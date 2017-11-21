package dev.olog.data.mapper

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import java.io.File

fun Song.toFolder() : Folder {
    return Folder(
            this.folder,
            this.path.substring(0, this.path.lastIndexOf(File.separator))
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
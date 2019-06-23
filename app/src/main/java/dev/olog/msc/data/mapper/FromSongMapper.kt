package dev.olog.msc.data.mapper

import android.content.Context
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song

fun Song.toFolder(context: Context, songCount: Int) : Folder {
    return Folder(
        this.folder.capitalize(),
        this.folderPath,
        songCount
    )
}

fun Song.toAlbum(songCount: Int) : Album {
    return Album(
        this.albumId,
        this.artistId,
        this.album,
        this.artist,
        this.albumArtist,
        songCount,
        this.hasAlbumNameAsFolder,
        false
    )
}

fun Song.toArtist(songCount: Int, albumsCount: Int) : Artist {
    return Artist(
        this.artistId,
        this.artist,
        this.albumArtist,
        songCount,
        albumsCount,
        false
    )
}
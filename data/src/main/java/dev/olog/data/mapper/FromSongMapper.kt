package dev.olog.data.mapper

import android.content.Context
import android.net.Uri
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import dev.olog.shared_android.ImagesFolderUtils

private val COVER_URI = Uri.parse("content://media/external/audio/albumart")

fun Song.toFolder(context: Context, songCount: Int) : Folder {
    val folderImage = ImagesFolderUtils.forFolder(context, this.folderPath)

    return Folder(
            this.folder,
            this.folderPath,
            songCount,
            folderImage
    )
}

fun Song.toAlbum(songCount: Int) : Album {
    return Album(
            this.albumId,
            this.artistId,
            this.album,
            this.artist,
            this.image,
            songCount
    )
}

fun Song.toNotNeuralAlbum() : Album {
    return Album(
            this.albumId,
            this.artistId,
            this.album,
            this.artist,
            ImagesFolderUtils.getOriginalAlbumCover(this.albumId).toString(),
            -1
    )
}

fun Song.toArtist(context: Context, songCount: Int, albumsCount: Int) : Artist {
    return Artist(
            this.artistId,
            this.artist,
            songCount,
            albumsCount,
            ImagesFolderUtils.forArtist(context, this.artistId)
    )
}
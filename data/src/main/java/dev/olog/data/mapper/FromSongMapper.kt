package dev.olog.data.mapper

import android.content.Context
import dev.olog.data.utils.FileUtils
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import java.io.File

fun Song.toFolder(context: Context, songCount: Int) : Folder {
    val normalizedPath = this.folderPath.replace(File.separator, "")
    val image = FileUtils.folderImagePath(context, normalizedPath)
    val file = File(image)

    return Folder(
            this.folder,
            this.folderPath,
            songCount,
            if (file.exists()) image else ""
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

fun Song.toArtist(context: Context, songCount: Int, albumsCount: Int) : Artist {
    val image = FileUtils.artistImagePath(context, this.artistId)
    val file = File(image)

    return Artist(
            this.artistId,
            this.artist,
            songCount,
            albumsCount,
            if (file.exists()) image else ""
    )
}
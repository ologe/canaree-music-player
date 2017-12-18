package dev.olog.data.mapper

import android.content.Context
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import java.io.File

fun Song.toFolder(context: Context, songCount: Int) : Folder {

    val image = "${context.applicationInfo.dataDir}${File.separator}folder"
    val file = File(image)
    val imageFile = if (file.exists()){
        val itemId = this.folderPath.replace(File.separator, "")
        file.listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == itemId }
    } else null

    return Folder(
            this.folder,
            this.folderPath,
            songCount,
            if (imageFile != null) imageFile.path else ""
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
    val image = "${context.applicationInfo.dataDir}${File.separator}artist"
    val file = File(image)
    val imageFile = if (file.exists()){
        file.listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == "${this.artistId}" }
    } else null

    return Artist(
            this.artistId,
            this.artist,
            songCount,
            albumsCount,
            if (imageFile != null) imageFile.path else ""
    )
}
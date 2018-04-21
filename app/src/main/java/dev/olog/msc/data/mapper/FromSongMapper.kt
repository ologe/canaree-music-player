package dev.olog.msc.data.mapper

import android.content.Context
import android.net.Uri
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.clamp

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

fun Song.toArtist(songCount: Int, albumsCount: Int) : Artist {
    return Artist(
            this.artistId,
            this.artist,
            songCount,
            albumsCount,
            ""
    )
}

fun Song.toFakeArtist(songCount: Int, albumsCount: Int) : Artist {
    return Artist(
            this.artistId,
            this.artist,
            songCount,
            albumsCount,
            getFakeImage(this.artistId)
    )
}

private fun getFakeImage(artistId: Long): String {
    val safe = clamp(artistId.rem(6), 0L, 6L)
    return Uri.parse("file:///android_asset/people/$safe.jpg").toString()
}
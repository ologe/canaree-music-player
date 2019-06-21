package dev.olog.msc.data.mapper

import android.content.Context
import android.net.Uri
import dev.olog.core.entity.Album
import dev.olog.core.entity.Artist
import dev.olog.core.entity.Folder
import dev.olog.core.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.shared.clamp

fun Song.toFolder(context: Context, songCount: Int) : Folder {
    val folderImage = ImagesFolderUtils.forFolder(context, this.folderPath)

    return Folder(
        this.folder.capitalize(),
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
        this.albumArtist,
        this.image,
        songCount,
        this.hasAlbumNameAsFolder
    )
}

fun Song.toArtist(songCount: Int, albumsCount: Int) : Artist {
    return Artist(
        this.artistId,
        this.artist,
        this.albumArtist,
        songCount,
        albumsCount,
        ""
    )
}

fun Song.toFakeArtist(songCount: Int, albumsCount: Int) : Artist {
    return Artist(
        this.artistId,
        this.artist,
        this.albumArtist,
        songCount,
        albumsCount,
        getFakeImage(this.artistId)
    )
}

internal fun getFakeImage(artistId: Long): String {
    val safe = clamp(artistId.rem(6), 0L, 6L)
    return Uri.parse("file:///android_asset/people/$safe.jpg").toString()
}
package dev.olog.data.mapper

import android.content.Context
import dev.olog.data.entity.LastPlayedAlbumEntity
import dev.olog.data.entity.LastPlayedArtistEntity
import dev.olog.data.utils.FileUtils
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import java.io.File

fun LastPlayedArtistEntity.toArtist(context: Context): Artist{
    val image = FileUtils.artistImagePath(context, this.id)
    val file = File(image)

    return Artist(
            this.id,
            this.name,
            -1,
            -1,
            if (file.exists()) image else ""
    )
}

fun LastPlayedAlbumEntity.toAlbum(): Album {
    return Album(
            this.id,
            this.artistId,
            this.title,
            this.artist,
            this.image,
            -1
    )
}
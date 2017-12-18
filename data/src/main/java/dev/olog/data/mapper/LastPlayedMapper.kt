package dev.olog.data.mapper

import android.content.Context
import dev.olog.data.entity.LastPlayedAlbumEntity
import dev.olog.data.entity.LastPlayedArtistEntity
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import java.io.File

fun LastPlayedArtistEntity.toArtist(context: Context): Artist{
    val image = "${context.applicationInfo.dataDir}${File.separator}artist"
    val file = File(image)
    val imageFile = if (file.exists()){
        file.listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == "${this.id}" }
    } else null

    return Artist(
            this.id,
            this.name,
            -1,
            -1,
            if (imageFile != null) imageFile.path else ""
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
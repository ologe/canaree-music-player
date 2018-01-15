package dev.olog.data.mapper

import android.content.Context
import dev.olog.data.ImageUtils
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import dev.olog.shared_android.Constants

fun Song.toFolder(context: Context, songCount: Int) : Folder {
    return Folder(
            this.folder,
            this.folderPath,
            songCount,
            getFolderImage(context, this.folderPath)
    )
}

private fun getFolderImage(context: Context, folderPath: String): String{
    if (Constants.useNeuralImages){
        val neuralImage = ImageUtils.getFolderNeuralImage(context, folderPath)
        if (neuralImage != null){
            return neuralImage
        }
    }
    return ImageUtils.getFolderImage(context, folderPath)
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
    return Artist(
            this.artistId,
            this.artist,
            songCount,
            albumsCount,
            getArtistImage(context, this.artistId)
    )
}

private fun getArtistImage(context: Context, artistId: Long): String{
    if (Constants.useNeuralImages){
        val neuralImage = ImageUtils.getArtistNeuralImage(context, artistId)
        if (neuralImage != null){
            return neuralImage
        }
    }
    return ImageUtils.getArtistImage(context, artistId)
}
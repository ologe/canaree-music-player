package dev.olog.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.data.ImageUtils
import dev.olog.data.utils.getLong
import dev.olog.data.utils.getString
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import dev.olog.shared_android.Constants

fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLong(android.provider.BaseColumns._ID)
    return Genre(
            id,
            this.getString(MediaStore.Audio.GenresColumns.NAME),
            genreSize,
            getGenreImage(context, id)
    )
}

private fun getGenreImage(context: Context, genreId: Long): String{
    if (Constants.useNeuralImages){
        val neuralImage = ImageUtils.getGenreNeuralImage(context, genreId)
        if (neuralImage != null){
            return neuralImage
        }
    }
    return ImageUtils.getGenreImage(context, genreId)
}

fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLong(android.provider.BaseColumns._ID)

    return Playlist(
            id,
            this.getString(MediaStore.Audio.PlaylistsColumns.NAME),
            playlistSize,
            getPlaylistImage(context, id)
    )
}

private fun getPlaylistImage(context: Context, playlistId: Long): String{
    if (Constants.useNeuralImages){
        val neuralImage = ImageUtils.getPlaylistNeuralImage(context, playlistId)
        if (neuralImage != null){
            return neuralImage
        }
    }
    return ImageUtils.getPlaylistImage(context, playlistId)
}

fun Cursor.extractId() : Long {
    return this.getLong(android.provider.BaseColumns._ID)
}
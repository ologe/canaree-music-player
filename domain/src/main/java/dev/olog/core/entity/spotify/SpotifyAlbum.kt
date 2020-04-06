package dev.olog.core.entity.spotify

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory.SPOTIFY_ALBUMS

data class SpotifyAlbum(
    val id: String,
    val title: String,
    val image: String,
    val songs: Int,
    val albumType: SpotifyAlbumType,
    val uri: String
) {

    val mediaId: MediaId.Category
        get() = MediaId.Category(SPOTIFY_ALBUMS, uri)

}
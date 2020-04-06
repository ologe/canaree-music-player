package dev.olog.domain.entity.spotify

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.SPOTIFY_ALBUMS

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
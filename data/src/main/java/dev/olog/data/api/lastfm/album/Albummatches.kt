package dev.olog.data.api.lastfm.album

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Albummatches(
    val album: List<Album>?,
) {

    @Serializable
    class Album(
        val name: String?,
        val artist: String?,
        val url: String?,
        val image: List<Image>?,
        val mbid: String?,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String?,
    )
}
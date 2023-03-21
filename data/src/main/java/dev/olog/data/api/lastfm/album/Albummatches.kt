package dev.olog.data.api.lastfm.album

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Albummatches(
    val album: List<Album>? = null,
) {

    @Serializable
    class Album(
        val name: String? = null,
        val artist: String? = null,
        val url: String? = null,
        val image: List<Image>? = null,
        val mbid: String? = null,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String? = null,
    )
}
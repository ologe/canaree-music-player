package dev.olog.data.api.lastfm.album

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AlbumInfo(
    val album: Album? = null,
) {

    @Serializable
    class Album(
        val name: String? = null,
        val artist: String? = null,
        val mbid: String? = null,
        val url: String? = null,
        val image: List<Image>? = null,
        val wiki: Wiki? = null,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String? = null,
        val size: String? = null,
    )

    @Serializable
    class Wiki(
        val published: String? = null,
        val summary: String? = null,
        val content: String? = null,
    )
}
package dev.olog.data.api.lastfm.album

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AlbumInfo(
    val album: Album?,
) {

    @Serializable
    class Album(
        val name: String?,
        val artist: String?,
        val mbid: String?,
        val url: String?,
        val image: List<Image>?,
        val wiki: Wiki?,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String?,
        val size: String?,
    )

    @Serializable
    class Wiki(
        val published: String?,
        val summary: String?,
        val content: String?,
    )
}
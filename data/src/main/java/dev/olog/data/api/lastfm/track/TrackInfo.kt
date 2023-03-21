package dev.olog.data.api.lastfm.track

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TrackInfo(
    val track: Track? = null
) {

    @Serializable
    class Track(
        val name: String? = null,
        val mbid: String? = null,
        val listeners: String? = null,
        val artist: Artist? = null,
        val album: Album? = null,
        val wiki: Wiki? = null,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String? = null,
        val size: String? = null,
    )

    @Serializable
    class Album(
        val artist: String? = null,
        val title: String? = null,
        val mbid: String? = null,
        val url: String? = null,
        val image: List<Image>? = null,
    )

    @Serializable
    class Artist(
        val name: String? = null,
        val mbid: String? = null,
        val url: String? = null,
    )

    @Serializable
    class Wiki(
        val summary: String? = null,
        val content: String? = null,
    )

}
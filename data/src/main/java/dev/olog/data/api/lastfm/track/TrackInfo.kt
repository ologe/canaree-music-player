package dev.olog.data.api.lastfm.track

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TrackInfo(
    val track: Track?
) {

    @Serializable
    class Track(
        val name: String?,
        val mbid: String?,
        val listeners: String?,
        val artist: Artist?,
        val album: Album?,
        val wiki: Wiki?,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String?,
        val size: String?,
    )

    @Serializable
    class Album(
        val artist: String?,
        val title: String?,
        val mbid: String?,
        val url: String?,
        val image: List<Image>?,
    )

    @Serializable
    class Artist(
        val name: String?,
        val mbid: String?,
        val url: String?,
    )

    @Serializable
    class Wiki(
        val summary: String?,
        val content: String?,
    )

}
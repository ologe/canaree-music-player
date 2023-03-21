package dev.olog.data.api.lastfm.track

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TrackSearch(
    val results: Results? = null,
) {

    @Serializable
    class Results(
        val trackmatches: Trackmatches? = null,
    )

    @Serializable
    class Trackmatches(
        val track: List<Track>? = null
    )

    @Serializable
    class Track(
        val name: String? = null,
        val artist: String? = null,
        val url: String? = null,
        val image: List<Image>? = null,
        val mbid: String? = null
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String? = null,
        val size: String? = null,
    )

}
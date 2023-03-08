package dev.olog.data.api.lastfm.track

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TrackSearch(
    val results: Results?,
) {

    @Serializable
    class Results(
        val trackmatches: Trackmatches?,
    )

    @Serializable
    class Trackmatches(
        val track: List<Track>?
    )

    @Serializable
    class Track(
        val name: String?,
        val artist: String?,
        val url: String?,
        val image: List<Image>?,
        val mbid: String?
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String?,
        val size: String?,
    )

}
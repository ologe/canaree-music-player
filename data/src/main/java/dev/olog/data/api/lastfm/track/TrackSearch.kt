package dev.olog.data.api.lastfm.track

import com.google.gson.annotations.SerializedName

class TrackSearch(
    val results: Results?,
) {

    class Results(
        val trackmatches: Trackmatches?,
    )

    class Trackmatches(
        val track: List<Track>?
    )

    class Track(
        val name: String?,
        val artist: String?,
        val url: String?,
        val image: List<Image>?,
        val mbid: String?
    )

    class Image(
        @SerializedName("#text")
        val text: String?,
        val size: String?,
    )

}
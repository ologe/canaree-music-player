package dev.olog.data.api.lastfm.track

import com.google.gson.annotations.SerializedName

class TrackInfo(
    val track: Track?
) {

    class Track(
        val name: String?,
        val mbid: String?,
        val listeners: String?,
        val artist: Artist?,
        val album: Album?,
        val wiki: Wiki?,
    )

    class Image(
        @SerializedName("#text")
        val text: String?,
        val size: String?,
    )

    class Album(
        val artist: String?,
        val title: String?,
        val mbid: String?,
        val url: String?,
        val image: List<Image>?,
    )

    class Artist(
        val name: String?,
        val mbid: String?,
        val url: String?,
    )

    class Wiki(
        val summary: String?,
        val content: String?,
    )

}
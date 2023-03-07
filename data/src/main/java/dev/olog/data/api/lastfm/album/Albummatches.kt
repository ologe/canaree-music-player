package dev.olog.data.api.lastfm.album

import com.google.gson.annotations.SerializedName

class Albummatches(
    val album: List<Album>?,
) {

    class Album(
        val name: String?,
        val artist: String?,
        val url: String?,
        val image: List<Image>?,
        val mbid: String?,
    )

    class Image(
        @SerializedName("#text")
        val text: String?,
    )
}
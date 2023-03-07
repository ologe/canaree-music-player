package dev.olog.data.api.lastfm.album

import com.google.gson.annotations.SerializedName

class AlbumInfo(
    val album: Album?,
) {

    class Album(
        val name: String?,
        val artist: String?,
        val mbid: String?,
        val url: String?,
        val image: List<Image>?,
        val wiki: Wiki?,
    )

    class Image(
        @SerializedName("#text")
        val text: String?,
        val size: String?,
    )

    class Wiki(
        val published: String?,
        val summary: String?,
        val content: String?,
    )
}
package dev.olog.data.api.lastfm.artist

import com.google.gson.annotations.SerializedName

class ArtistInfo(
    val artist: Artist?,
) {

    class Artist(
        val name: String?,
        val mbid: String?,
        val url: String?,
        val image: List<Image>?,
        val bio: Bio?,
    )

    class Image(
        @SerializedName("#text")
        val text: String?,
        val size: String?,
    )

    class Bio(
        val links: Links?,
        val published: String?,
        val summary: String?,
        val content: String?,
    )

    class Links(
        val link: Link?,
    )

    class Link(
        @SerializedName("#text")
        val text: String?,
        val rel: String?,
        val href: String?,
    )

}
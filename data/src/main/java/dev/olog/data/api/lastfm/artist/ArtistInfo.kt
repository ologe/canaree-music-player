package dev.olog.data.api.lastfm.artist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ArtistInfo(
    val artist: Artist?,
) {

    @Serializable
    class Artist(
        val name: String?,
        val mbid: String?,
        val url: String?,
        val image: List<Image>?,
        val bio: Bio?,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String?,
        val size: String?,
    )

    @Serializable
    class Bio(
        val links: Links?,
        val published: String?,
        val summary: String?,
        val content: String?,
    )

    @Serializable
    class Links(
        val link: Link?,
    )

    @Serializable
    class Link(
        @SerialName("#text")
        val text: String?,
        val rel: String?,
        val href: String?,
    )

}
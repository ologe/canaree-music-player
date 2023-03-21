package dev.olog.data.api.lastfm.artist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ArtistInfo(
    val artist: Artist? = null,
) {

    @Serializable
    class Artist(
        val name: String? = null,
        val mbid: String? = null,
        val url: String? = null,
        val image: List<Image>? = null,
        val bio: Bio? = null,
    )

    @Serializable
    class Image(
        @SerialName("#text")
        val text: String? = null,
        val size: String? = null,
    )

    @Serializable
    class Bio(
        val links: Links? = null,
        val published: String? = null,
        val summary: String? = null,
        val content: String? = null,
    )

    @Serializable
    class Links(
        val link: Link? = null,
    )

    @Serializable
    class Link(
        @SerialName("#text")
        val text: String? = null,
        val rel: String? = null,
        val href: String? = null,
    )

}
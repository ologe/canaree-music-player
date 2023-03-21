package dev.olog.data.api.deezer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeezerArtistResponse(
    val data: List<Album>? = null,
) {

    @Serializable
    class Album(
        val picture: String? = null,
        @SerialName("picture_big")
        val pictureBig: String? = null,
        @SerialName("picture_medium")
        val pictureMedium: String? = null,
        @SerialName("picture_small")
        val pictureSmall: String? = null,
        @SerialName("picture_xl")
        val pictureXl: String? = null,
    )
}
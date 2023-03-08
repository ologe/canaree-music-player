package dev.olog.data.api.deezer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeezerArtistResponse(
    val data: List<Album>?,
) {

    @Serializable
    class Album(
        val picture: String?,
        @SerialName("picture_big")
        val pictureBig: String?,
        @SerialName("picture_medium")
        val pictureMedium: String?,
        @SerialName("picture_small")
        val pictureSmall: String?,
        @SerialName("picture_xl")
        val pictureXl: String?,
    )
}
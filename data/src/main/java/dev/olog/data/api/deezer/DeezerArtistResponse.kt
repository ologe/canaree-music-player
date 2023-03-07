package dev.olog.data.api.deezer

import com.google.gson.annotations.SerializedName

class DeezerArtistResponse(
    val data: List<Album>?,
) {

    class Album(
        val picture: String?,
        @SerializedName("picture_big")
        val pictureBig: String?,
        @SerializedName("picture_medium")
        val pictureMedium: String?,
        @SerializedName("picture_small")
        val pictureSmall: String?,
        @SerializedName("picture_xl")
        val pictureXl: String?,
    )
}
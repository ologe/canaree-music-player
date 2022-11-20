package dev.olog.data.api.deezer

import com.google.gson.annotations.SerializedName

data class DeezerArtistResponse(
    val data: List<Album> = emptyList()
) {

    // xl -> 1000px
    // big -> 500px
    // medium -> 250px
    // small -> 56px
    data class Album(
        @SerializedName("picture_xl")
        val pictureXl: String? = null,
        @SerializedName("picture_big")
        val pictureBig: String? = null,
        @SerializedName("picture_medium")
        val pictureMedium: String? = null,
        @SerializedName("picture_small")
        val pictureSmall: String? = null,
    )
}
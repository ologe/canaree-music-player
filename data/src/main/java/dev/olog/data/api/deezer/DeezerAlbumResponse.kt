package dev.olog.data.api.deezer

import com.google.gson.annotations.SerializedName

data class DeezerAlbumResponse(
    val data: List<Data> = emptyList()
) {

    // xl -> 1000px
    // big -> 500px
    // medium -> 250px
    // small -> 56px
    data class Data(
        @SerializedName("cover_xl")
        val coverXl: String? = null,
        @SerializedName("cover_big")
        val coverBig: String? = null,
        @SerializedName("cover_medium")
        val coverMedium: String? = null,
        @SerializedName("cover_small")
        val coverSmall: String? = null,
    )
}
package dev.olog.data.api.deezer

import com.google.gson.annotations.SerializedName

data class DeezerTrackResponse(
    val data: List<Data> = emptyList()
) {

    data class Data(
        val album: Album? = null,
    )

    // xl -> 1000px
    // big -> 500px
    // medium -> 250px
    // small -> 56px
    data class Album(
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
package dev.olog.data.api.deezer

import com.google.gson.annotations.SerializedName

class DeezerTrackResponse(
    val data: List<Data>,
) {

    class Data(
        val album: Album?,
    )

    class Album(
        val cover: String?,
        @SerializedName("cover_big")
        val coverBig: String?,
        @SerializedName("cover_medium")
        val coverMedium: String?,
        @SerializedName("cover_small")
        val coverSmall: String?,
        @SerializedName("cover_xl")
        val coverXl: String?,
    )
}
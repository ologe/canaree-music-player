package dev.olog.data.api.deezer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeezerTrackResponse(
    val data: List<Data>? = null,
) {

    @Serializable
    class Data(
        val album: Album? = null,
    )

    @Serializable
    class Album(
        val cover: String? = null,
        @SerialName("cover_big")
        val coverBig: String? = null,
        @SerialName("cover_medium")
        val coverMedium: String? = null,
        @SerialName("cover_small")
        val coverSmall: String? = null,
        @SerialName("cover_xl")
        val coverXl: String? = null,
    )
}
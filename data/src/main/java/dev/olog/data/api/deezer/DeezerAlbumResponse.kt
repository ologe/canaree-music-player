package dev.olog.data.api.deezer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeezerAlbumResponse(
    val data: List<Data>? = null,
) {

    @Serializable
    class Data(
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
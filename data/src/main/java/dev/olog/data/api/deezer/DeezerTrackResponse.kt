package dev.olog.data.api.deezer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeezerTrackResponse(
    val data: List<Data>,
) {

    @Serializable
    class Data(
        val album: Album?,
    )

    @Serializable
    class Album(
        val cover: String?,
        @SerialName("cover_big")
        val coverBig: String?,
        @SerialName("cover_medium")
        val coverMedium: String?,
        @SerialName("cover_small")
        val coverSmall: String?,
        @SerialName("cover_xl")
        val coverXl: String?,
    )
}
package dev.olog.data.api.deezer.entity

import com.google.gson.annotations.SerializedName

internal data class DeezerArtistSearchResultDto(
    @SerializedName("data") val items: List<DeezerArtistDto>?,
    val next: String?,
    val total: Int?
) {

    val bestPicture: String?
        get() = items?.firstOrNull()?.bestPicture

    companion object {

        val EMPTY: DeezerArtistSearchResultDto
            get() = DeezerArtistSearchResultDto(
                items = emptyList(),
                next = null,
                total = null
            )


    }

}
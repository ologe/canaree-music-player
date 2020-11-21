package dev.olog.data.api.deezer.entity

import com.google.gson.annotations.SerializedName

internal data class DeezerAlbumSearchResultDto(
    @SerializedName("data") val items: List<DeezerAlbumDto>?,
    val next: String?,
    val total: Int?
) {

    val bestCover: String?
        get() = items?.firstOrNull()?.bestCover

    companion object {

        val EMPTY: DeezerAlbumSearchResultDto
            get() = DeezerAlbumSearchResultDto(
                items = null,
                next = null,
                total = null
            )

    }

}
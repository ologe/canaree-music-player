package dev.olog.data.remote.deezer.dto

import com.google.gson.annotations.SerializedName

internal data class DeezerTrackSearchResultDto(
    @SerializedName("name") val items: List<DeezerTrackDto>?,
    val next: String?,
    val total: Int?
) {

    val bestCover: String?
        get() = items?.firstOrNull()?.album?.bestCover

    companion object {

        val EMPTY: DeezerTrackSearchResultDto
            get() = DeezerTrackSearchResultDto(
                items = null,
                next = null,
                total = null
            )



    }

}
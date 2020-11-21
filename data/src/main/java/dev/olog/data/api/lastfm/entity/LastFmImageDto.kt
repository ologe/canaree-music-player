package dev.olog.data.api.lastfm.entity

import com.google.gson.annotations.SerializedName

internal data class LastFmImageDto(
    @SerializedName("#text") val text: String?,
    val size: String?
) {

    companion object {

        val EMPTY: LastFmImageDto
            get() = LastFmImageDto(
                text = null,
                size = null
            )



    }

}
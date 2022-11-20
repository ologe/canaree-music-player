package dev.olog.data.api.lastfm

import com.google.gson.annotations.SerializedName

data class LastFmImageDto(
    @SerializedName("#text")
    val text: String? = null,
    val size: String? = null,
)
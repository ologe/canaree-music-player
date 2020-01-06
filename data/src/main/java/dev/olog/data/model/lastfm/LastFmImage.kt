package dev.olog.data.model.lastfm

import com.google.gson.annotations.SerializedName

data class LastFmImage(
    @SerializedName("#text") val text: String,
    val size: String
)
package dev.olog.data.model.lastfm

import com.google.gson.annotations.SerializedName

data class LastFmWiki(
    @SerializedName(value = "content", alternate = ["summary"])
    val content: String? = null
)
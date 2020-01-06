package dev.olog.data.model.deezer

import com.google.gson.annotations.SerializedName

data class DeezerDataAlbum(
    val cover: String? = null,
    @SerializedName("cover_big") val coverBig: String? = null,
    @SerializedName("cover_medium") val coverMedium: String? = null,
    @SerializedName("cover_small") val coverSmall: String? = null,
    @SerializedName("cover_xl") val coverXl: String? = null
) {

    fun getBestImage(): String {
        return when {
            !coverXl.isNullOrBlank() -> coverXl
            !coverBig.isNullOrBlank() -> coverBig
            !coverMedium.isNullOrBlank() -> coverMedium
            !coverSmall.isNullOrBlank() -> coverSmall
            !cover.isNullOrBlank() -> cover
            else -> ""
        }
    }

}
package dev.olog.data.model.deezer

import com.google.gson.annotations.SerializedName

data class DeezerDataAlbum(
    val cover: String? = null,
    @SerializedName("cover_big") val coverBig: String?,
    @SerializedName("cover_medium") val coverMedium: String?,
    @SerializedName("cover_small") val coverSmall: String?,
    @SerializedName("cover_xl") val coverXl: String?
) {

    fun getBestImage(): String? {
        return when {
            !coverXl.isNullOrBlank() -> coverXl
            !coverBig.isNullOrBlank() -> coverBig
            !coverMedium.isNullOrBlank() -> coverMedium
            !coverSmall.isNullOrBlank() -> coverSmall
            !cover.isNullOrBlank() -> cover
            else -> null
        }
    }

}
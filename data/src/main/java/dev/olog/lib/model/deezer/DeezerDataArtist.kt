package dev.olog.lib.model.deezer

import com.google.gson.annotations.SerializedName

data class DeezerDataArtist(
    val picture: String? = null,
    @SerializedName("picture_big") val pictureBig: String? = null,
    @SerializedName("picture_medium") val pictureMedium: String? = null,
    @SerializedName("picture_small") val pictureSmall: String? = null,
    @SerializedName("picture_xl") val pictureXl: String? = null
) {


    fun getBestImage(): String {
        return when {
            !pictureXl.isNullOrBlank() -> pictureXl
            !pictureBig.isNullOrBlank() -> pictureBig
            !pictureMedium.isNullOrBlank() -> pictureMedium
            !pictureSmall.isNullOrBlank() -> pictureSmall
            !picture.isNullOrBlank() -> picture
            else -> ""
        }
    }

}
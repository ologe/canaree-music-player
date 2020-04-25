package dev.olog.data.model.deezer

import com.google.gson.annotations.SerializedName

data class DeezerDataArtist(
    val picture: String? = null,
    @SerializedName("picture_big") val pictureBig: String?,
    @SerializedName("picture_medium") val pictureMedium: String?,
    @SerializedName("picture_small") val pictureSmall: String?,
    @SerializedName("picture_xl") val pictureXl: String?
) {


    fun getBestImage(): String? {
        return when {
            !pictureXl.isNullOrBlank() -> pictureXl
            !pictureBig.isNullOrBlank() -> pictureBig
            !pictureMedium.isNullOrBlank() -> pictureMedium
            !pictureSmall.isNullOrBlank() -> pictureSmall
            !picture.isNullOrBlank() -> picture
            else -> null
        }
    }

}